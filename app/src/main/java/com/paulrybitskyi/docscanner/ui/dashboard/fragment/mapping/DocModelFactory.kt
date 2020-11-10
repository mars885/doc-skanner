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

package com.paulrybitskyi.docscanner.ui.dashboard.fragment.mapping

import com.paulrybitskyi.docscanner.ui.views.DocModel
import com.paulrybitskyi.docscanner.utils.DocDateFormatter
import java.io.File


internal interface DocModelFactory {

    fun createModel(document: File): DocModel

}


internal class DocModelFactoryImpl(
    private val docDateFormatter: DocDateFormatter
) : DocModelFactory {


    override fun createModel(document: File): DocModel {
        return DocModel(
            name = document.name,
            date = docDateFormatter.formatDate(document.lastModified())
        )
    }


}