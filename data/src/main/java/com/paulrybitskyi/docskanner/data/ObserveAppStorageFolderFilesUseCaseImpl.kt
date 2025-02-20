/*
 * Copyright 2021 Paul Rybitskyi, oss@paulrybitskyi.com
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

import android.os.FileObserver
import com.paulrybitskyi.commons.ktx.fileList
import com.paulrybitskyi.commons.ktx.newFileObserver
import com.paulrybitskyi.docskanner.core.providers.AppStorageFolderProvider
import com.paulrybitskyi.docskanner.core.providers.DispatcherProvider
import com.paulrybitskyi.docskanner.domain.ObserveAppStorageFolderFilesUseCase
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BindType
internal class ObserveAppStorageFolderFilesUseCaseImpl @Inject constructor(
    private val appStorageFolderProvider: AppStorageFolderProvider,
    private val dispatcherProvider: DispatcherProvider
) : ObserveAppStorageFolderFilesUseCase {


    private companion object {

        private const val FILE_EVENTS = (
            FileObserver.MODIFY or FileObserver.ATTRIB or
            FileObserver.MOVED_FROM or FileObserver.MOVED_TO or
            FileObserver.CREATE or FileObserver.DELETE or
            FileObserver.DELETE_SELF
        )

    }


    override suspend fun execute(params: Unit): Flow<List<File>> {
        return callbackFlow {
            val appStorageFolder = appStorageFolderProvider.getAppStorageFolder()
            val fileObserver = appStorageFolder.newFileObserver(FILE_EVENTS) { _, _ ->
                offer(appStorageFolder.fileList())
            }

            fileObserver.startWatching()
            offer(appStorageFolder.fileList())

            awaitClose { fileObserver.stopWatching() }
        }
        .flowOn(dispatcherProvider.computation)
    }


}