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

package com.paulrybitskyi.docskanner.ui.views.doccropper

import android.graphics.Bitmap
import android.graphics.PointF
import com.paulrybitskyi.docskanner.ui.views.imagecropper.CroppingCoords
import com.paulrybitskyi.docskanner.ui.views.imagecropper.fromList
import com.paulrybitskyi.docskanner.ui.views.imagecropper.toList
import com.paulrybitskyi.docskanner.utils.ImagePerspectiveTransformer
import com.squareup.picasso.Transformation

internal class CroppingTransformation(
    private val imagePerspectiveTransformer: ImagePerspectiveTransformer,
    private val croppingCoords: CroppingCoords,
    private val viewSize: Pair<Float, Float>
) : Transformation {


    override fun key(): String {
        return buildString {
            append("Cropping. ")
            append("Highlight: $croppingCoords. ")
            append("View Size: $viewSize.")
        }
    }


    override fun transform(source: Bitmap): Bitmap {
        val xRatio = (source.width.toFloat() / viewSize.first)
        val yRatio = (source.height.toFloat() / viewSize.second)

        val scaledCoords = croppingCoords
            .toList()
            .map {
                val x = (it.x * xRatio)
                val y = (it.y * yRatio)

                PointF(x, y)
            }
            .let(::fromList)

        val imagePerspectiveCoords = ImagePerspectiveTransformer.ImagePerspectiveCoords(
            topLeftCoord = scaledCoords.topLeftCoord,
            topRightCoord = scaledCoords.topRightCoord,
            bottomLeftCoord = scaledCoords.bottomLeftCoord,
            bottomRightCoord = scaledCoords.bottomRightCoord
        )

        val finalBitmap = imagePerspectiveTransformer.transformPerspective(
            image = source,
            perspectiveCoords = imagePerspectiveCoords
        )

        if(finalBitmap != source) {
            source.recycle()
        }

        return finalBitmap
    }


}