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

package com.paulrybitskyi.docskanner.ui.views.doceffects.transformations

import android.graphics.*
import com.paulrybitskyi.docskanner.utils.ImageEffectApplier
import com.squareup.picasso.Transformation

internal class GrayscaleTransformation(
    private val imageEffectApplier: ImageEffectApplier
) : Transformation {


    override fun key() = "Grayscale"


    override fun transform(source: Bitmap): Bitmap {
        return imageEffectApplier.applyGrayscaleEffect(source)
            .also { if(it != source) source.recycle() }
    }


}