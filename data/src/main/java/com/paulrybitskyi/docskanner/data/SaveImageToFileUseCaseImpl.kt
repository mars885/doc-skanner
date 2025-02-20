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

import android.graphics.Bitmap
import com.paulrybitskyi.docskanner.core.providers.DispatcherProvider
import com.paulrybitskyi.docskanner.domain.SaveImageToFileUseCase
import com.paulrybitskyi.docskanner.domain.SaveImageToFileUseCase.Params
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BindType
internal class SaveImageToFileUseCaseImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) : SaveImageToFileUseCase {


    override suspend fun execute(params: Params): Flow<Unit> {
        return flow<Unit> {
            params.destinationFile.outputStream()
                .also { params.image.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                .also(OutputStream::close)
        }
        .flowOn(dispatcherProvider.io)
    }


}