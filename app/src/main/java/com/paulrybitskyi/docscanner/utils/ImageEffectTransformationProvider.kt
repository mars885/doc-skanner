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

package com.paulrybitskyi.docscanner.utils

import com.paulrybitskyi.docscanner.ui.views.doceffects.transformations.BinaryTransformation
import com.paulrybitskyi.docscanner.ui.views.doceffects.transformations.GrayscaleTransformation
import com.paulrybitskyi.docscanner.ui.views.doceffects.transformations.MagicColorTransformation
import javax.inject.Provider


internal interface ImageEffectTransformationProvider {

    fun provideMagicColorTransformation(): MagicColorTransformation

    fun provideGrayscaleTransformation(): GrayscaleTransformation

    fun provideBinaryTransformation(): BinaryTransformation

}


internal class ImageEffectTransformationProviderImpl(
    private val imageEffectApplier: Provider<ImageEffectApplier>
) : ImageEffectTransformationProvider {


    override fun provideMagicColorTransformation(): MagicColorTransformation {
        return MagicColorTransformation(imageEffectApplier.get())
    }


    override fun provideGrayscaleTransformation(): GrayscaleTransformation {
        return GrayscaleTransformation(imageEffectApplier.get())
    }


    override fun provideBinaryTransformation(): BinaryTransformation {
        return BinaryTransformation(imageEffectApplier.get())
    }


}