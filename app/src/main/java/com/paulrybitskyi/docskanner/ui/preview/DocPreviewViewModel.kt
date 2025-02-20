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

package com.paulrybitskyi.docskanner.ui.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docskanner.ui.Constants
import com.paulrybitskyi.docskanner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


private const val PARAM_DOC_FILE = "doc_file"


@HiltViewModel
internal class DocPreviewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {


    private val _toolbarTitle = MutableLiveData<String>()
    private val _docFile = MutableLiveData<File>()

    val toolbarTitle: LiveData<String> = _toolbarTitle
    val docFile: LiveData<File> = _docFile


    init {
        initData()
    }


    private fun initData() {
        viewModelScope.launch {
            val file = checkNotNull(savedStateHandle.get<File>(PARAM_DOC_FILE))

            _toolbarTitle.value = file.name
            delay(Constants.DEFAULT_WINDOW_ANIMATION_DURATION)
            _docFile.value = file
        }
    }


    fun onToolbarLeftButtonClicked() {
        route(DocPreviewRoute.NavigateBack)
    }


}