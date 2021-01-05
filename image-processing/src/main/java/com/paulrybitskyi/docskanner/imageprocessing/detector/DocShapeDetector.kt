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

package com.paulrybitskyi.docskanner.imageprocessing.detector

import android.graphics.Bitmap
import android.graphics.PointF
import com.paulrybitskyi.docskanner.imageprocessing.utils.*
import com.paulrybitskyi.docskanner.imageprocessing.utils.toMat
import com.paulrybitskyi.docskanner.imageprocessing.utils.toPointF
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import javax.inject.Inject


interface DocShapeDetector {

    fun detectShape(image: Bitmap): DocShape

}


internal class OpenCvDocShapeDetector @Inject constructor(
    private val docCoordsOrderer: DocCoordsOrderer
) : DocShapeDetector {


    private companion object {

        private const val DOWNSCALED_IMAGE_SIZE = 600f

        private const val GAUSSIAN_BLUR_KERNEL_SIZE = 5.0

        private const val CANNY_THRESHOLD_MIN = 75.0
        private const val CANNY_THRESHOLD_MAX = 200.0

        private const val RECT_SIDE_COUNT = 4

        private const val LARGE_CONTOURS_LIMIT_COUNT = 5

        private const val CONTOUR_APPROX_ACCURACY_PERCENTAGE = 2.0

    }


    override fun detectShape(image: Bitmap): DocShape {
        val largestRectangleCoords = findLargestRectangleCoords(image)
        val docShape = docCoordsOrderer.order(largestRectangleCoords)

        return (docShape ?: getWholeImageShape(image))
    }


    private fun findLargestRectangleCoords(image: Bitmap): List<PointF> {
        val imageMat = image.toMat()
        val maxDimension = imageMat.width().coerceAtLeast(imageMat.height()).toDouble()
        val scaleRatio = (DOWNSCALED_IMAGE_SIZE / maxDimension)
        val downscaledImageMat = calculateDownscaledMatrix(scaleRatio, imageMat)
        val largestRectangleMat = (findLargestRectangleMat(downscaledImageMat) ?: return emptyList())
        val scaledLargestRectangleMat = scaleRectangle(largestRectangleMat, (1.0 / scaleRatio))

        return scaledLargestRectangleMat.toList()
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


    private fun findLargestRectangleMat(imageMat: Mat): MatOfPoint2f? {
        val grayscaleImageMat = applyGrayscaleEffect(imageMat)
        val smoothedImageMat = applyGaussianBlur(grayscaleImageMat)
        val imageEdgesMat = findImageEdges(smoothedImageMat)
        val imageContours = findImageContours(imageEdgesMat)
        val largestRectContour = findLargestRectangularContour(imageContours)

        return largestRectContour
    }


    private fun applyGrayscaleEffect(imageMat: Mat): Mat {
        return imageMat.applyGrayscaleEffect()
            .also { imageMat.release() }
    }


    private fun applyGaussianBlur(imageMat: Mat): Mat {
        return imageMat.applyGaussianBlur(GAUSSIAN_BLUR_KERNEL_SIZE)
            .also { imageMat.release() }
    }


    private fun findImageEdges(imageMat: Mat): Mat {
        return imageMat.findCannyEdges(CANNY_THRESHOLD_MIN, CANNY_THRESHOLD_MAX)
            .also { imageMat.release() }
    }


    private fun findImageContours(edgesMat: Mat): List<MatOfPoint> {
        return edgesMat.findContours()
            .also { edgesMat.release() }
    }


    private fun findLargestRectangularContour(contours: List<MatOfPoint>): MatOfPoint2f? {
        val largeContours = contours.sortedByDescending(Imgproc::contourArea)
            .take(LARGE_CONTOURS_LIMIT_COUNT)

        for(largeContour in largeContours) {
            val approxCurve = approximatePolygonalCurve(largeContour)

            if(approxCurve.rows() == RECT_SIDE_COUNT) {
                return approxCurve
            }
        }

        return null
    }


    private fun approximatePolygonalCurve(contour: MatOfPoint): MatOfPoint2f {
        return contour.approxPolyCurve(
            accuracyPercentage = CONTOUR_APPROX_ACCURACY_PERCENTAGE,
            isCurveClosed = true
        )
    }


    private fun scaleRectangle(rectangle: MatOfPoint2f, scale: Double): MatOfPoint2f {
        val originalRecCoords = rectangle.toList()
        val resultRecCoords = originalRecCoords.map {
            Point(it.x * scale, it.y * scale)
        }

        return MatOfPoint2f().apply { fromList(resultRecCoords) }
    }


    private fun getWholeImageShape(image: Bitmap): DocShape {
        return DocShape(
            topLeftCoord = PointF(0f, 0f),
            topRightCoord = PointF(image.width.toFloat(), 0f),
            bottomLeftCoord = PointF(0f, image.height.toFloat()),
            bottomRightCoord = PointF(image.width.toFloat(), image.height.toFloat())
        )
    }


}