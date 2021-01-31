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

import com.paulrybitskyi.docskanner.core.formatters.DocDateFormatter
import com.paulrybitskyi.docskanner.core.formatters.DocSizeFormatter
import com.paulrybitskyi.hiltbinder.BindType
import java.io.File
import javax.inject.Inject


interface DocDetailsBuilder {

    fun buildDetails(document: File): String?

}


@BindType
internal class DocDetailsBuilderImpl @Inject constructor(
    private val docDateFormatter: DocDateFormatter,
    private val docSizeFormatter: DocSizeFormatter
) : DocDetailsBuilder {


    private companion object {

        private const val INFO_SEPARATOR = " • "

    }


    override fun buildDetails(document: File): String? {
        val date = docDateFormatter.formatDate(document.lastModified())
        val size = docSizeFormatter.formatSize(document)

        if((date == null) && (size == null)) return null

        return buildString {
            date?.let(::append)

            if(size != null) {
                append(INFO_SEPARATOR)
                append(size)
            }
        }
    }


}