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
import com.paulrybitskyi.docscanner.data.*
import com.paulrybitskyi.docscanner.data.ClearAppCacheUseCaseImpl
import com.paulrybitskyi.docscanner.data.CreateAppStorageFolderUseCaseImpl
import com.paulrybitskyi.docscanner.data.CreatePdfDocumentUseCaseImpl
import com.paulrybitskyi.docscanner.data.ObserveAppStorageFolderFilesUseCaseImpl
import com.paulrybitskyi.docscanner.data.SaveBitmapToFileUseCaseUseCaseImpl
import com.paulrybitskyi.docscanner.domain.*
import com.paulrybitskyi.docscanner.domain.ClearAppCacheUseCase
import com.paulrybitskyi.docscanner.domain.CreateAppStorageFolderUseCase
import com.paulrybitskyi.docscanner.domain.CreatePdfDocumentUseCase
import com.paulrybitskyi.docscanner.domain.ObserveAppStorageFolderFilesUseCase
import com.paulrybitskyi.docscanner.domain.SaveBitmapToFileUseCase
import com.paulrybitskyi.docscanner.utils.AppStorageFolderProvider
import com.paulrybitskyi.docscanner.utils.DispatcherProvider
import com.paulrybitskyi.docscanner.utils.OpenCvManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal object UseCasesModule {


    @Singleton
    @Provides
    fun provideInitOpenCvLibraryUseCase(
        openCvManager: OpenCvManager
    ): InitOpenCvLibraryUseCase {
        return InitOpenCvLibraryUseCaseImpl(openCvManager)
    }


    @Singleton
    @Provides
    fun provideCreateAppStorageFolderUseCase(
        appStorageFolderProvider: AppStorageFolderProvider,
        dispatcherProvider: DispatcherProvider
    ): CreateAppStorageFolderUseCase {
        return CreateAppStorageFolderUseCaseImpl(
            appStorageFolderProvider = appStorageFolderProvider,
            dispatcherProvider = dispatcherProvider
        )
    }


    @Singleton
    @Provides
    fun provideClearAppCacheUseCase(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider
    ): ClearAppCacheUseCase {
        return ClearAppCacheUseCaseImpl(
            applicationContext = context,
            dispatcherProvider = dispatcherProvider
        )
    }


    @Singleton
    @Provides
    fun provideObserveAppStorageFolderFilesUseCase(
        appStorageFolderProvider: AppStorageFolderProvider,
        dispatcherProvider: DispatcherProvider
    ): ObserveAppStorageFolderFilesUseCase {
        return ObserveAppStorageFolderFilesUseCaseImpl(
            appStorageFolderProvider = appStorageFolderProvider,
            dispatcherProvider = dispatcherProvider
        )
    }


    @Singleton
    @Provides
    fun provideSaveBitmapToFileUseCase(dispatcherProvider: DispatcherProvider): SaveBitmapToFileUseCase {
        return SaveBitmapToFileUseCaseUseCaseImpl(dispatcherProvider)
    }


    @Singleton
    @Provides
    fun provideCreatePdfDocumentUseCase(
        dispatcherProvider: DispatcherProvider
    ): CreatePdfDocumentUseCase {
        return CreatePdfDocumentUseCaseImpl(dispatcherProvider)
    }


}