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

package com.paulrybitskyi.docscanner.ui.preview

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.paulrybitskyi.docscanner.ui.base.BaseViewModel
import java.io.File


private const val PARAM_DOC_FILE_PATH = "doc_file_path"


internal class DocPreviewViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {


    private val _docFile = MutableLiveData<File>()

    val toolbarTitle: LiveData<String> = Transformations.map(_docFile, File::getName)
    val docFile: LiveData<File> = _docFile


    init {
        _docFile.value = createFile()
    }


    private fun createFile(): File {
        val docFilePath = checkNotNull(savedStateHandle.get<String>(PARAM_DOC_FILE_PATH))
        val docFile = File(docFilePath)

        return docFile
    }


    fun onToolbarLeftButtonClicked() {
        route(DocPreviewRoutes.NavigateBack)
    }


}