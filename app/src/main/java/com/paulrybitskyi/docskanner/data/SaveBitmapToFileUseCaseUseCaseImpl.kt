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

import android.graphics.Bitmap
import com.paulrybitskyi.docskanner.domain.SaveBitmapToFileUseCase
import com.paulrybitskyi.docskanner.domain.SaveBitmapToFileUseCase.Params
import com.paulrybitskyi.docskanner.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.OutputStream

internal class SaveBitmapToFileUseCaseUseCaseImpl(
    private val dispatcherProvider: DispatcherProvider
) : SaveBitmapToFileUseCase {


    override suspend fun execute(params: Params): Flow<Unit> {
        return flow {
            params.destinationFile.outputStream()
                .also { params.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                .also(OutputStream::close)

            emit(Unit)
        }
        .flowOn(dispatcherProvider.io)
    }


}