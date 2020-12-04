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

import android.os.Environment
import java.io.File
import javax.inject.Inject


interface AppStorageFolderProvider {

    fun getAppStorageFolder(): File

}


internal class AppStorageFolderProviderImpl @Inject constructor() : AppStorageFolderProvider {


    private companion object {

        private const val APP_STORAGE_FOLDER_NAME = "DocsSkanner"

    }


    override fun getAppStorageFolder(): File {
        val docsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val docsFolderPath = docsFolder.absolutePath
        val appStorageFolderPath = (docsFolderPath + File.separator + APP_STORAGE_FOLDER_NAME)
        val appStorageFolder = File(appStorageFolderPath)

        return appStorageFolder
    }


}