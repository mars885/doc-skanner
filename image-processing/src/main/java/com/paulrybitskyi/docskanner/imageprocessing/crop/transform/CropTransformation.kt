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

package com.paulrybitskyi.docskanner.imageprocessing.crop.transform

import android.graphics.Bitmap
import android.graphics.PointF
import com.paulrybitskyi.docskanner.imageloading.Transformation
import com.paulrybitskyi.docskanner.imageprocessing.crop.ImagePerspectiveTransformer

internal class CropTransformation(
    private val imagePerspectiveTransformer: ImagePerspectiveTransformer,
    private val cropCoords: CropCoords,
    private val viewSize: Size
) : Transformation {


    override val key: String
        get() = "Crop. Coords: $cropCoords. View Size: $viewSize."


    override fun transform(source: Bitmap): Bitmap {
        val xRatio = (source.width.toFloat() / viewSize.width)
        val yRatio = (source.height.toFloat() / viewSize.height)

        val scaledCoords = cropCoords.toList()
            .map {
                val x = (it.x * xRatio)
                val y = (it.y * yRatio)

                PointF(x, y)
            }
            .let(::fromList)

        val imagePerspectiveCoords = ImagePerspectiveTransformer.InputCoords(
            topLeftCoord = scaledCoords.topLeftCoord,
            topRightCoord = scaledCoords.topRightCoord,
            bottomLeftCoord = scaledCoords.bottomLeftCoord,
            bottomRightCoord = scaledCoords.bottomRightCoord
        )

        val finalBitmap = imagePerspectiveTransformer.transformPerspective(
            image = source,
            inputCoords = imagePerspectiveCoords
        )

        if(finalBitmap != source) {
            source.recycle()
        }

        return finalBitmap
    }


}