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
import com.paulrybitskyi.docscanner.utils.*
import com.paulrybitskyi.docscanner.utils.DocDateFormatter
import com.paulrybitskyi.docscanner.utils.DocDateFormatterImpl
import com.paulrybitskyi.docscanner.utils.StringProviderImpl
import com.paulrybitskyi.docscanner.utils.dialogs.DialogBuilder
import com.paulrybitskyi.docscanner.utils.dialogs.DialogBuilderImpl
import com.paulrybitskyi.docscanner.utils.highlight.ImageHighlightFinder
import com.paulrybitskyi.docscanner.utils.highlight.OpenCvImageHighlightFinder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
internal object CoreModule {


    @Provides
    fun provideOpenCvManager(): OpenCvManager {
        return OpenCvManagerImpl()
    }


    @Provides
    fun provideStringProvider(@ApplicationContext context: Context): StringProvider {
        return StringProviderImpl(context)
    }


    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }


    @Provides
    fun providePermissionVerifier(@ApplicationContext context: Context): PermissionVerifier {
        return PermissionVerifierImpl(context)
    }


    @Provides
    fun provideCameraPresenceVerifier(@ApplicationContext context: Context): CameraPresenceVerifier {
        return CameraPresenceVerifierImpl(context)
    }


    @Provides
    fun provideTemporaryImageFileCreator(
        @ApplicationContext context: Context
    ): TemporaryImageFileCreator {
        return TemporaryImageFileCreatorImpl(context)
    }


    @Provides
    fun providePdfDocumentFileCreator(
        appStorageFolderProvider: AppStorageFolderProvider
    ): PdfDocumentFileCreator {
        return PdfDocumentFileCreatorImpl(appStorageFolderProvider)
    }


    @Provides
    fun provideShareableUriFactory(@ApplicationContext context: Context): ShareableUriFactory {
        return ShareableUriFactoryImpl(context)
    }


    @Provides
    fun provideDialogBuilder(): DialogBuilder {
        return DialogBuilderImpl()
    }


    @Provides
    fun provideAppStorageFolderProvider(): AppStorageFolderProvider {
        return AppStorageFolderProviderImpl()
    }


    @Provides
    fun provideDocDetailsBuilder(
        docDateFormatter: DocDateFormatter,
        docSizeFormatter: DocSizeFormatter
    ): DocDetailsBuilder {
        return DocDetailsBuilderImpl(
            docDateFormatter = docDateFormatter,
            docSizeFormatter = docSizeFormatter
        )
    }


    @Provides
    fun provideDocDateFormatter(@ApplicationContext context: Context): DocDateFormatter {
        return DocDateFormatterImpl(context)
    }


    @Provides
    fun provideDocSizeFormatter(
        @ApplicationContext context: Context,
    ): DocSizeFormatter {
        return DocSizeFormatterImpl(applicationContext = context)
    }


    @Provides
    fun provideImageHighlightFinder(): ImageHighlightFinder {
        return OpenCvImageHighlightFinder()
    }


    @Provides
    fun provideImagePerspectiveTransformer(): ImagePerspectiveTransformer {
        return OpenCvImagePerspectiveTransformer()
    }


    @Provides
    fun provideImageEffectApplier(): ImageEffectApplier {
        return OpenCvImageEffectApplier()
    }


}