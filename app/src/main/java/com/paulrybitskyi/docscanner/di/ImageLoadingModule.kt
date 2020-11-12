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

package com.paulrybitskyi.docscanner.di

import android.content.Context
import android.graphics.Bitmap
import com.paulrybitskyi.docscanner.utils.*
import com.paulrybitskyi.docscanner.utils.CroppingTransformationFactory
import com.paulrybitskyi.docscanner.utils.CroppingTransformationFactoryImpl
import com.paulrybitskyi.docscanner.utils.ImageEffectApplier
import com.paulrybitskyi.docscanner.utils.ImageEffectTransformationProvider
import com.paulrybitskyi.docscanner.utils.ImageEffectTransformationProviderImpl
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal object ImageLoadingModule {


    @Singleton
    @Provides
    fun providePicasso(@ApplicationContext context: Context): Picasso {
        // Disabling cache
        val cacheSizeInBytes = 1

        return Picasso.Builder(context)
            .defaultBitmapConfig(Bitmap.Config.ARGB_8888)
            .memoryCache(LruCache(cacheSizeInBytes))
            .build()
    }


    @Provides
    fun provideImageEffectTransformationProvider(
        imageEffectApplier: Provider<ImageEffectApplier>
    ): ImageEffectTransformationProvider {
        return ImageEffectTransformationProviderImpl(imageEffectApplier)
    }


    @Provides
    fun provideCroppingTransformationFactory(
        imagePerspectiveTransformer: Provider<ImagePerspectiveTransformer>
    ): CroppingTransformationFactory {
        return CroppingTransformationFactoryImpl(imagePerspectiveTransformer)
    }


}