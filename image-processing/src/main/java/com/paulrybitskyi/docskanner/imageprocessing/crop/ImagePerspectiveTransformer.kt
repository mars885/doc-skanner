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

package com.paulrybitskyi.docskanner.imageprocessing.crop

import android.graphics.Bitmap
import android.graphics.PointF
import com.paulrybitskyi.docskanner.imageprocessing.crop.ImagePerspectiveTransformer.SourceShapeCoords
import com.paulrybitskyi.docskanner.imageprocessing.utils.toBitmap
import com.paulrybitskyi.docskanner.imageprocessing.utils.toMat
import com.paulrybitskyi.docskanner.imageprocessing.utils.toPoint
import com.paulrybitskyi.hiltbinder.BindType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import javax.inject.Inject
import kotlin.math.sqrt


interface ImagePerspectiveTransformer {

    data class SourceShapeCoords(
        val topLeftCoord: PointF,
        val topRightCoord: PointF,
        val bottomLeftCoord: PointF,
        val bottomRightCoord: PointF
    )

    fun transformPerspective(sourceImage: Bitmap, sourceShapeCoords: SourceShapeCoords): Bitmap

}


@BindType
internal class OpenCvImagePerspectiveTransformer @Inject constructor() : ImagePerspectiveTransformer {


    override fun transformPerspective(sourceImage: Bitmap, sourceShapeCoords: SourceShapeCoords): Bitmap {
        val sourceImageMat = sourceImage.toMat()
        val sourceShapeCoordsMat = sourceShapeCoords.toMat()
        val destImageSize = computeDestinationImageSize(sourceShapeCoords)
        val destImageMat = Mat.zeros(destImageSize, sourceImageMat.type())
        val destShapeCoordsMat = computeDestinationShapeCoordsMat(destImageMat)
        val transformationMat = Imgproc.getPerspectiveTransform(sourceShapeCoordsMat, destShapeCoordsMat)

        Imgproc.warpPerspective(sourceImageMat, destImageMat, transformationMat, destImageSize)

        return destImageMat.toBitmap()
            .also { sourceImageMat.release() }
    }


    private fun SourceShapeCoords.toMat(): MatOfPoint2f {
        val shapeCoordsArray = arrayOf(
            topLeftCoord.toPoint(),
            topRightCoord.toPoint(),
            bottomLeftCoord.toPoint(),
            bottomRightCoord.toPoint()
        )
        val shapeCoordsMat = MatOfPoint2f().apply { fromArray(*shapeCoordsArray) }

        return shapeCoordsMat
    }


    private fun computeDestinationImageSize(coords: SourceShapeCoords): Size {
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


    private fun computeDestinationShapeCoordsMat(imageMat: Mat): MatOfPoint2f {
        val topLeftCoord = Point(0.0, 0.0)
        val topRightCoord = Point(imageMat.cols().toDouble(), 0.0)
        val bottomLeftCoord = Point(0.0, imageMat.rows().toDouble())
        val bottomRightCoord = Point(imageMat.cols().toDouble(), imageMat.rows().toDouble())
        val coords = arrayOf(topLeftCoord, topRightCoord, bottomLeftCoord, bottomRightCoord)

        return MatOfPoint2f().apply { fromArray(*coords) }
    }


}