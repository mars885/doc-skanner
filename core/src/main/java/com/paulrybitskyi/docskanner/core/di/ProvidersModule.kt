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

package com.paulrybitskyi.docskanner.core.di

import com.paulrybitskyi.docskanner.core.providers.*
import com.paulrybitskyi.docskanner.core.providers.AppStorageFolderProviderImpl
import com.paulrybitskyi.docskanner.core.providers.DispatcherProviderImpl
import com.paulrybitskyi.docskanner.core.providers.StringProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
internal interface ProvidersModule {

    @Binds
    fun bindAppStorageFolderProvider(provider: AppStorageFolderProviderImpl): AppStorageFolderProvider

    @Binds
    fun bindDispatcherProvider(provider: DispatcherProviderImpl): DispatcherProvider

    @Binds
    fun bindStringProvider(provider: StringProviderImpl): StringProvider

}