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

import android.graphics.pdf.PdfDocument
import com.paulrybitskyi.docskanner.domain.CreatePdfDocumentUseCase
import com.paulrybitskyi.docskanner.domain.CreatePdfDocumentUseCase.Params
import com.paulrybitskyi.docskanner.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class CreatePdfDocumentUseCaseImpl(
    private val dispatcherProvider: DispatcherProvider
) : CreatePdfDocumentUseCase {


    override suspend fun execute(params: Params): Flow<Unit> {
        return flow {
            val bitmap = params.bitmap

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
                .apply { canvas.drawBitmap(bitmap, 0f, 0f, null) }

            pdfDocument.finishPage(page)
            pdfDocument.writeTo(params.destinationFile.outputStream())
            pdfDocument.close()

            emit(Unit)
        }
        .flowOn(dispatcherProvider.io)
    }


}