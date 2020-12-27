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

package com.paulrybitskyi.docskanner.core

import com.paulrybitskyi.docskanner.core.providers.AppStorageFolderProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


interface PdfDocumentFileCreator {

    fun createDefaultPdfFileName(): String

    fun createPdfFile(fileName: String): File

}


internal class PdfDocumentFileCreatorImpl @Inject constructor(
    private val appStorageFolderProvider: AppStorageFolderProvider
) : PdfDocumentFileCreator {


    private companion object {

        private const val FILE_NAME_PREFIX = "DocSkanner "
        private const val FILE_NAME_EXTENSION = ".pdf"
        private const val FILE_NAME_PATTERN = "dd-MM-yyyy HH:mm:ss"

    }


    override fun createDefaultPdfFileName(): String {
        return buildString {
            append(FILE_NAME_PREFIX)
            append(SimpleDateFormat(FILE_NAME_PATTERN, Locale.getDefault()).format(Date()))
            append(FILE_NAME_EXTENSION)
        }
    }


    override fun createPdfFile(fileName: String): File {
        val appStorageFolder = appStorageFolderProvider.getAppStorageFolder()
        val file = File(appStorageFolder, fileName).also(File::createNewFile)

        return file
    }


}