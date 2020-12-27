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

package com.paulrybitskyi.docskanner.core.formatters

import android.content.Context
import android.text.format.Formatter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


interface DocSizeFormatter {

    fun formatSize(document: File): String?

}


internal class DocSizeFormatterImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : DocSizeFormatter {


    override fun formatSize(document: File): String? {
        if(document.isDirectory) return null

        return Formatter.formatFileSize(applicationContext, document.length())
    }


}