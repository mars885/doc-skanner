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

package com.paulrybitskyi.docskanner.imageprocessing.resize

import android.graphics.Bitmap
import com.paulrybitskyi.docskanner.imageloading.Transformation
import kotlin.math.roundToInt

internal class ResizeTransformation(
    private val maxWidth: Int,
    private val maxHeight: Int
) : Transformation {


    override val key = "Resize. Max Width: $maxWidth. Max Height: $maxHeight."


    override fun transform(source: Bitmap): Bitmap {
        val optimalSize = calculateOptimalImageSize(source)
        val resizedBitmap = Bitmap.createScaledBitmap(source, optimalSize.width, optimalSize.height, false)

        if(resizedBitmap != source) {
            source.recycle()
        }

        return resizedBitmap
    }


    private fun calculateOptimalImageSize(image: Bitmap): Size {
        val bitmapWidth = image.width
        val bitmapHeight = image.height

        val aspectRatio = (bitmapWidth.toFloat() / bitmapHeight.toFloat())
        val adjustedWidth = (aspectRatio * maxHeight).roundToInt()

        if(adjustedWidth <= maxWidth) {
            return Size(adjustedWidth, maxHeight)
        }

        val finalWidth = maxWidth
        val finalHeight = ((maxWidth.toFloat() / adjustedWidth.toFloat()) * maxHeight).roundToInt()

        return Size(finalWidth, finalHeight)
    }


    private class Size(
        val width: Int,
        val height: Int
    )


}