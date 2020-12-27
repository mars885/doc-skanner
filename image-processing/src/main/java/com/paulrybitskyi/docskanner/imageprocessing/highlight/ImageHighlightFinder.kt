/*
 * Copyright 2020 Paul Rybitskyi, paul.rybitskyi.work@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paulrybitskyi.docskanner.imageprocessing.highlight

import android.graphics.Bitmap
import android.graphics.PointF
import com.paulrybitskyi.docskanner.imageprocessing.utils.toMat
import com.paulrybitskyi.docskanner.imageprocessing.utils.toMatOfPoint
import com.paulrybitskyi.docskanner.imageprocessing.utils.toMatOfPoint2f
import com.paulrybitskyi.docskanner.imageprocessing.utils.toPointF
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt


interface ImageHighlightFinder {

    fun findImageHighlight(image: Bitmap): ImageHighlight

}


internal class OpenCvImageHighlightFinder @Inject constructor() : ImageHighlightFinder {


    private companion object {

        private const val DOWNSCALED_IMAGE_SIZE = 600f

        private const val BLUR_KERNEL_SIZE = 9

        private const val MAX_THRESHOLD_LEVEL = 2
        private const val MAX_THRESHOLD_VALUE = 255.0

        private const val CANNY_THRESHOLD_MIN = 10.0
        private const val CANNY_THRESHOLD_MAX = 20.0

        private const val AREA_LOWER_THRESHOLD = 0.2
        private const val AREA_UPPER_THRESHOLD = 0.98

        private val RECTANGLE_COMPARATOR_BY_DESCENDING_AREA = { mat1: Mat, mat2: Mat ->
            val area1 = Imgproc.contourArea(mat1)
            val area2 = Imgproc.contourArea(mat2)

            ceil(area2 - area1).toInt()
        }

    }


    override fun findImageHighlight(image: Bitmap): ImageHighlight {
        val imageHighlightCoords = findLargestRectangleCoords(image)
        val imageHighlight = ImageHighlightFactory.fromCoordinates(imageHighlightCoords)
        val finalImageHighlight = if(imageHighlight.isValid) {
            imageHighlight
        } else {
            getImageEdgesAsHighlight(image)
        }

        return finalImageHighlight
    }


    private fun findLargestRectangleCoords(image: Bitmap): List<PointF> {
        val imageMat = image.toMat()
        val maxDimension = imageMat.width().coerceAtLeast(imageMat.height()).toDouble()
        val scaleRatio = (DOWNSCALED_IMAGE_SIZE / maxDimension)
        val downscaledImageMat = calculateDownscaledMatrix(scaleRatio, imageMat)
        val rectangles = findLargestRectangleCoords(downscaledImageMat)

        if(rectangles.isEmpty()) return emptyList()

        val sortedRectangles = rectangles.sortedWith(RECTANGLE_COMPARATOR_BY_DESCENDING_AREA)
        val largestRectangle = sortedRectangles[0]
        val scaledLargestRectangle = scaleRectangle(
            largestRectangle,
            (1.0 / scaleRatio)
        )
        return scaledLargestRectangle
            .toList()
            .map(Point::toPointF)
    }


    private fun calculateDownscaledMatrix(scaleRatio: Double, imageMat: Mat): Mat {
        val newWidth = (imageMat.width() * scaleRatio)
        val newHeight = (imageMat.height() * scaleRatio)
        val downscaledSize = Size(newWidth, newHeight)
        val downscaledMat = Mat(downscaledSize, imageMat.type())

        Imgproc.resize(imageMat, downscaledMat, downscaledSize)

        return downscaledMat
    }


    private fun findLargestRectangleCoords(imageMat: Mat): List<MatOfPoint2f> {
        /*
            1. Blur the image to filter out the noise.
            2. If threshold level == 1
                then Canny + Dilate
                else Binary Threshold
            3. Find contours.
            4. Filter contours that resemble rectangles.
            5. Return a list of contours that resemble rectangles.
         */

        // Blur the image to filter out the noise.
        val blurredImageMat = applyMedianBlur(imageMat)

        // Set up images to use.
        val gray0 = Mat(blurredImageMat.size(), CvType.CV_8U)
        val gray = Mat()

        // For Core.mixChannels.
        val contours = mutableListOf<MatOfPoint>()
        val rectangles = mutableListOf<MatOfPoint2f>()

        val sources = buildList { add(blurredImageMat) }
        val destinations = buildList { add(gray0) }

        // To filter rectangles by their areas.
        val imageArea = (imageMat.rows() * imageMat.cols())

        // Find squares in every color plane of the image.
        for(channel in 0..2) {
            mixChannels(sources, destinations, channel)

            // Try several threshold levels.
            for(thresholdLevel in 0 until MAX_THRESHOLD_LEVEL) {
                if(thresholdLevel == 0) {
                    applyCannyEdgeDetection(imageMat = gray0, edgesMat = gray)
                    applyDilation(imageMat = gray)
                } else {
                    applyBinaryThreshold(source = gray0, destination = gray, thresholdLevel)
                }

                findContours(imageMat = gray, contours)

                for(contour in contours) {
                    val approxCurve = approximatePolygonalCurve(contour)

                    if(isRectangle(approxCurve, imageArea)) {
                        rectangles.add(approxCurve)
                    }
                }
            }
        }

        return rectangles
    }


    private fun applyMedianBlur(imageMat: Mat): Mat {
        return Mat().also { Imgproc.medianBlur(imageMat, it, BLUR_KERNEL_SIZE) }
    }


    private fun mixChannels(sources: List<Mat>, destinations: List<Mat>, channel: Int) {
        val ch = intArrayOf(channel, 0)
        val fromTo = MatOfInt(*ch)

        Core.mixChannels(sources, destinations, fromTo)
    }


    private fun applyCannyEdgeDetection(imageMat: Mat, edgesMat: Mat) {
        // HACK: Use Canny instead of zero threshold level.
        // Canny helps to catch squares with gradient shading.
        // NOTE: No kernel size parameters on Java API.
        Imgproc.Canny(imageMat, edgesMat, CANNY_THRESHOLD_MIN, CANNY_THRESHOLD_MAX)
    }


    private fun applyDilation(imageMat: Mat) {
        // Dilate Canny output to remove potential holes between edge segments.
        val dilateKernel = Mat.ones(Size(3.0, 3.0), 0)

        Imgproc.dilate(imageMat, imageMat, dilateKernel)
    }


    private fun applyBinaryThreshold(source: Mat, destination: Mat, thresholdLevel: Int) {
        val threshold = ((thresholdLevel + 1) * MAX_THRESHOLD_VALUE / MAX_THRESHOLD_LEVEL)

        Imgproc.threshold(
            source,
            destination,
            threshold,
            MAX_THRESHOLD_VALUE,
            Imgproc.THRESH_BINARY
        )
    }


    private fun findContours(imageMat: Mat, contours: MutableList<MatOfPoint>) {
        // Find contours and store them all as a list.
        Imgproc.findContours(
            imageMat,
            contours,
            Mat(),  // hierarchy - not needed
            Imgproc.RETR_LIST,  // mode
            Imgproc.CHAIN_APPROX_SIMPLE // approximation method
        )
    }


    private fun approximatePolygonalCurve(contour: MatOfPoint): MatOfPoint2f {
        val contourFloat = contour.toMatOfPoint2f()
        val isCurveClosed = true
        val contourPerimeter = Imgproc.arcLength(
            contourFloat,
            true    // isCurveClosed
        )

        // Approximate polygonal curves.
        val approxCurve = MatOfPoint2f()
        val epsilon = (contourPerimeter * 0.02)

        Imgproc.approxPolyDP(contourFloat, approxCurve, epsilon, isCurveClosed)

        return approxCurve
    }


    private fun isRectangle(polygon: MatOfPoint2f, imageArea: Int): Boolean {
        val polygonInt = polygon.toMatOfPoint()

        if(polygon.rows() != 4) {
            return false
        }

        val polygonArea = abs(Imgproc.contourArea(polygon))

        if((polygonArea < (imageArea * AREA_LOWER_THRESHOLD)) ||
            (polygonArea > (imageArea * AREA_UPPER_THRESHOLD))) {
            return false
        }

        if(!Imgproc.isContourConvex(polygonInt)) {
            return false
        }

        // Check if the all angles are more than 72.54 degrees (cos 0.3).
        val approxPoints = polygon.toArray()
        var maxCosine = 0.0
        var cosine: Double

        for(i in 2..4) {
            cosine = abs(
                calculateAngle(
                    approxPoints[i % 4],
                    approxPoints[i - 2],
                    approxPoints[i - 1]
                )
            )

            maxCosine = cosine.coerceAtLeast(maxCosine)
        }

        return (maxCosine <= 0.3)
    }


    private fun calculateAngle(point1: Point, point2: Point, point0: Point): Double {
        val dx1 = (point1.x - point0.x)
        val dy1 = (point1.y - point0.y)

        val dx2 = (point2.x - point0.x)
        val dy2 = (point2.y - point0.y)

        val nominator = ((dx1 * dx2) + (dy1 * dy2))
        val denominator = sqrt(((dx1 * dx1) + (dy1 * dy1)) * ((dx2 * dx2) + (dy2 * dy2)) + 1e-10)

        return (nominator / denominator)
    }


    private fun scaleRectangle(rectangle: MatOfPoint2f, scale: Double): MatOfPoint2f {
        val originalRecCoords = rectangle.toList()
        val resultRecCoords = originalRecCoords.map {
            Point(it.x * scale, it.y * scale)
        }

        return MatOfPoint2f().apply { fromList(resultRecCoords) }
    }


    private fun getImageEdgesAsHighlight(image: Bitmap): ImageHighlight {
        return ImageHighlight(
            topLeftCoord = PointF(0f, 0f),
            topRightCoord = PointF(image.width.toFloat(), 0f),
            bottomLeftCoord = PointF(0f, image.height.toFloat()),
            bottomRightCoord = PointF(image.width.toFloat(), image.height.toFloat())
        )
    }


}