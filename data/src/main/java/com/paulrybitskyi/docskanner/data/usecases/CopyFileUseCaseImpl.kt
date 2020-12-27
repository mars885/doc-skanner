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

package com.paulrybitskyi.docskanner.data.usecases

import android.content.Context
import com.paulrybitskyi.docskanner.core.providers.DispatcherProvider
import com.paulrybitskyi.docskanner.domain.CopyFileUseCase
import com.paulrybitskyi.docskanner.domain.CopyFileUseCase.Params
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CopyFileUseCaseImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val dispatcherProvider: DispatcherProvider
) : CopyFileUseCase {


    override suspend fun execute(params: Params): Flow<Unit> {
        return flow<Unit> {
            val contentResolver = applicationContext.contentResolver
            val sourceFileStream = contentResolver.openInputStream(params.source)
            val destFileStream = contentResolver.openOutputStream(params.destination)

            if((sourceFileStream != null) && (destFileStream != null)) {
                sourceFileStream.copyTo(destFileStream)

                sourceFileStream.close()
                destFileStream.close()
            }
        }
        .flowOn(dispatcherProvider.io)
    }


}