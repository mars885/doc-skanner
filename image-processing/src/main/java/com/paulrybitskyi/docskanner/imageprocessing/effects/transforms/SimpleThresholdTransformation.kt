/*
 * Copyright 2020 Paul Rybitskyi, oss@paulrybitskyi.com
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

package com.paulrybitskyi.docskanner.imageprocessing.effects.transforms

import android.graphics.Bitmap
import com.paulrybitskyi.docskanner.imageloading.Transformation
import com.paulrybitskyi.docskanner.imageprocessing.effects.ImageEffectApplier

internal class SimpleThresholdTransformation(
    private val imageEffectApplier: ImageEffectApplier
) : Transformation {


    override val key = "SimpleThreshold"


    override fun transform(source: Bitmap): Bitmap {
        return imageEffectApplier.applySimpleThresholdEffect(source)
            .also { if(it != source) source.recycle() }
    }


}