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

package com.paulrybitskyi.docskanner.di

import android.content.Context
import com.paulrybitskyi.docskanner.data.*
import com.paulrybitskyi.docskanner.data.ClearAppCacheUseCaseImpl
import com.paulrybitskyi.docskanner.data.CreateAppStorageFolderUseCaseImpl
import com.paulrybitskyi.docskanner.data.CreatePdfDocumentUseCaseImpl
import com.paulrybitskyi.docskanner.data.ObserveAppStorageFolderFilesUseCaseImpl
import com.paulrybitskyi.docskanner.data.SaveBitmapToFileUseCaseUseCaseImpl
import com.paulrybitskyi.docskanner.domain.*
import com.paulrybitskyi.docskanner.domain.ClearAppCacheUseCase
import com.paulrybitskyi.docskanner.domain.CreateAppStorageFolderUseCase
import com.paulrybitskyi.docskanner.domain.CreatePdfDocumentUseCase
import com.paulrybitskyi.docskanner.domain.ObserveAppStorageFolderFilesUseCase
import com.paulrybitskyi.docskanner.domain.SaveBitmapToFileUseCase
import com.paulrybitskyi.docskanner.utils.AppStorageFolderProvider
import com.paulrybitskyi.docskanner.utils.DispatcherProvider
import com.paulrybitskyi.docskanner.utils.OpenCvManager
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