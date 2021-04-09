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

package com.paulrybitskyi.docskanner.ui.scanner

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.core.factories.TemporaryImageFileFactory
import com.paulrybitskyi.docskanner.core.providers.StringProvider
import com.paulrybitskyi.docskanner.core.utils.onError
import com.paulrybitskyi.docskanner.core.utils.onSuccess
import com.paulrybitskyi.docskanner.domain.SaveImageToFileUseCase
import com.paulrybitskyi.docskanner.ui.Constants
import com.paulrybitskyi.docskanner.ui.base.BaseViewModel
import com.paulrybitskyi.docskanner.ui.base.events.commons.GeneralCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


private const val PARAM_DOC_IMAGE_FILE = "doc_image_file"


@HiltViewModel
internal class DocScannerViewModel @Inject constructor(
    private val saveImageToFileUseCase: SaveImageToFileUseCase,
    private val temporaryImageFileFactory: TemporaryImageFileFactory,
    private val stringProvider: StringProvider,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {


    val docImageFile: LiveData<File> = liveData {
        delay(Constants.DEFAULT_WINDOW_ANIMATION_DURATION)
        emit(checkNotNull(savedStateHandle.get<File>(PARAM_DOC_IMAGE_FILE)))
    }


    fun onToolbarLeftButtonClicked() {
        route(DocScannerRoute.NavigateBack)
    }


    fun onRotateLeftButtonClicked() {
        dispatchCommand(DocScannerCommand.RotateImageLeft)
    }


    fun onRotateRightButtonClicked() {
        dispatchCommand(DocScannerCommand.RotateImageRight)
    }


    fun onConfirmButtonClicked(scannedDocument: Bitmap) {
        saveScannedDocumentToFile(scannedDocument)
    }


    private fun saveScannedDocumentToFile(scannedDocument: Bitmap) {
        viewModelScope.launch {
            val scannedDocImageFile = temporaryImageFileFactory.createTempImageFile()
            val params = SaveImageToFileUseCase.Params(scannedDocument, scannedDocImageFile)

            saveImageToFileUseCase.execute(params)
                .onSuccess { onScannedDocumentSaved(scannedDocImageFile) }
                .onError {
                    val message = stringProvider.getString(R.string.error_image_to_file_conversion)
                    dispatchCommand(GeneralCommand.ShowLongToast(message))
                }
                .collect()
        }
    }


    private fun onScannedDocumentSaved(scannedDocImageFile: File) {
        route(DocScannerRoute.DocEditor(scannedDocImageFile))
    }


}