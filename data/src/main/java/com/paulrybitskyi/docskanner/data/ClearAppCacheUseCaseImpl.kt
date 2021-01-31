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

package com.paulrybitskyi.docskanner.data

import android.content.Context
import com.paulrybitskyi.commons.ktx.fileList
import com.paulrybitskyi.docskanner.core.providers.DispatcherProvider
import com.paulrybitskyi.docskanner.domain.ClearAppCacheUseCase
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BindType
internal class ClearAppCacheUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val dispatcherProvider: DispatcherProvider
) : ClearAppCacheUseCase {


    override suspend fun execute(params: Unit): Flow<Unit> {
        return flow<Unit> {
            val cacheFolder = applicationContext.cacheDir
            val cacheFiles = cacheFolder.fileList()

            for(cacheFile in cacheFiles) {
                if(cacheFile.exists()) cacheFile.delete()
            }
        }
        .flowOn(dispatcherProvider.io)
    }


}