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

package com.paulrybitskyi.docskanner.data

import com.paulrybitskyi.docskanner.core.providers.AppStorageFolderProvider
import com.paulrybitskyi.docskanner.core.providers.DispatcherProvider
import com.paulrybitskyi.docskanner.domain.CreateAppStorageFolderUseCase
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BindType
internal class CreateAppStorageFolderUseCaseImpl @Inject constructor(
    private val appStorageFolderProvider: AppStorageFolderProvider,
    private val dispatcherProvider: DispatcherProvider
) : CreateAppStorageFolderUseCase {


    override suspend fun execute(params: Unit): Flow<Unit> {
        return flow<Unit> {
            val appStorageFolder = appStorageFolderProvider.getAppStorageFolder()

            if(!appStorageFolder.exists()) {
                appStorageFolder.mkdirs()
            }

            if(!appStorageFolder.exists()) {
                throw IllegalStateException("Could not create the app's storage folder.")
            }
        }
        .flowOn(dispatcherProvider.io)
    }


}