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

package com.paulrybitskyi.docskanner.utils

import android.graphics.Bitmap
import android.graphics.PointF
import com.paulrybitskyi.docskanner.utils.ImagePerspectiveTransformer.ImagePerspectiveCoords
import com.paulrybitskyi.docskanner.utils.extensions.toBitmap
import com.paulrybitskyi.docskanner.utils.extensions.toMat
import com.paulrybitskyi.docskanner.utils.extensions.toPoint
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.sqrt


internal interface ImagePerspectiveTransformer {

    data class ImagePerspectiveCoords(
        val topLeftCoord: PointF,
        val topRightCoord: PointF,
        val bottomLeftCoord: PointF,
        val bottomRightCoord: PointF
    )

    fun transformPerspective(
        image: Bitmap,
        perspectiveCoords: ImagePerspectiveCoords
    ): Bitmap

}


internal class OpenCvImagePerspectiveTransformer : ImagePerspectiveTransformer {


    override fun transformPerspective(
        image: Bitmap,
        perspectiveCoords: ImagePerspectiveCoords
    ): Bitmap {
        val imageMat = image.toMat()
        val cornerPointMat = createCornerCoordsMat(perspectiveCoords)
        val rectangleSize = calculateRectangleSize(perspectiveCoords)
        val resultImageMat = Mat.zeros(rectangleSize, imageMat.type())
        val imageOutline = calculateImageOutline(resultImageMat)
        val transformationMat = Imgproc.getPerspectiveTransform(cornerPointMat, imageOutline)

        Imgproc.warpPerspective(imageMat, resultImageMat, transformationMat, rectangleSize)

        return resultImageMat.toBitmap()
    }


    private fun createCornerCoordsMat(perspectiveCoords: ImagePerspectiveCoords): MatOfPoint2f {
        val coordsArray = arrayOf(
            perspectiveCoords.topLeftCoord.toPoint(),
            perspectiveCoords.topRightCoord.toPoint(),
            perspectiveCoords.bottomLeftCoord.toPoint(),
            perspectiveCoords.bottomRightCoord.toPoint()
        )
        val coordsMat = MatOfPoint2f().apply { fromArray(*coordsArray) }

        return coordsMat
    }


    private fun calculateRectangleSize(coords: ImagePerspectiveCoords): Size {
        val topDistance = calculateDistance(coords.topLeftCoord.toPoint(), coords.topRightCoord.toPoint())
        val bottomDistance = calculateDistance(coords.bottomLeftCoord.toPoint(), coords.bottomRightCoord.toPoint())
        val leftDistance = calculateDistance(coords.topLeftCoord.toPoint(), coords.bottomLeftCoord.toPoint())
        val rightDistance = calculateDistance(coords.topRightCoord.toPoint(), coords.bottomRightCoord.toPoint())

        val averageWidth = ((topDistance + bottomDistance) / 2f)
        val averageHeight = ((leftDistance + rightDistance) / 2f)

        return Size(averageWidth, averageHeight)
    }


    private fun calculateDistance(point1: Point, point2: Point): Double {
        val dx = (point2.x - point1.x)
        val dy = (point2.y - point1.y)

        return sqrt((dx * dx) + (dy * dy))
    }


    private fun calculateImageOutline(imageMat: Mat): MatOfPoint2f {
        val topLeftCoord = Point(0.0, 0.0)
        val topRightCoord = Point(imageMat.cols().toDouble(), 0.0)
        val bottomLeftCoord = Point(0.0, imageMat.rows().toDouble())
        val bottomRightCoord = Point(imageMat.cols().toDouble(), imageMat.rows().toDouble())
        val coords = arrayOf(topLeftCoord, topRightCoord, bottomLeftCoord, bottomRightCoord)

        return MatOfPoint2f().apply { fromArray(*coords) }
    }


}