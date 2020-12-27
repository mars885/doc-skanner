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

package com.paulrybitskyi.docskanner.core.verifiers

import javax.inject.Inject


enum class FileExtension(internal val type: String) {

    PDF("pdf")

}


interface FileNameExtensionVerifier {

    fun hasExtension(fileName: String, extension: FileExtension): Boolean

}


internal class FileNameExtensionVerifierImpl @Inject constructor() : FileNameExtensionVerifier {


    override fun hasExtension(fileName: String, extension: FileExtension): Boolean {
        if(fileName.isBlank()) return false

        return fileName.endsWith(".${extension.type}")
    }


}