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

package com.paulrybitskyi.docscanner.ui.editing

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docscanner.R
import com.paulrybitskyi.docscanner.domain.SaveBitmapToFileUseCase
import com.paulrybitskyi.docscanner.ui.Constants
import com.paulrybitskyi.docscanner.ui.base.BaseViewModel
import com.paulrybitskyi.docscanner.ui.base.events.commons.GeneralCommands
import com.paulrybitskyi.docscanner.utils.StringProvider
import com.paulrybitskyi.docscanner.utils.TemporaryImageFileCreator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.io.File


private const val PARAM_DOC_FILE = "doc_file"


internal class DocEditingViewModel @ViewModelInject constructor(
    private val saveBitmapToFileUseCase: SaveBitmapToFileUseCase,
    private val temporaryImageFileCreator: TemporaryImageFileCreator,
    private val stringProvider: StringProvider,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {


    val docFile: LiveData<Uri> = liveData {
        delay(Constants.DEFAULT_WINDOW_ANIMATION_DURATION)
        emit(checkNotNull(savedStateHandle.get<Uri>(PARAM_DOC_FILE)))
    }


    fun onToolbarLeftButtonClicked() {
        route(DocEditingRoutes.NavigateBack)
    }


    fun onRotateLeftButtonClicked() {
        dispatchCommand(DocEditingCommands.RotateImageLeft)
    }


    fun onRotateRightButtonClicked() {
        dispatchCommand(DocEditingCommands.RotateImageRight)
    }


    fun onNextButtonClicked(croppedDocument: Bitmap) {
        saveCroppedDocumentToFile(croppedDocument)
    }


    private fun saveCroppedDocumentToFile(croppedDocument: Bitmap) {
        viewModelScope.launch {
            val croppedDocFile = temporaryImageFileCreator.createTempImageFile()
            val params = SaveBitmapToFileUseCase.Params(croppedDocument, croppedDocFile)

            saveBitmapToFileUseCase.execute(params)
                .onCompletion { onCroppedDocumentSaved(it, croppedDocFile) }
                .catch {
                    val message = stringProvider.getString(R.string.error_bitmap_saving)
                    dispatchCommand(GeneralCommands.ShowLongToast(message))
                }
                .collect()
        }
    }


    private fun onCroppedDocumentSaved(error: Throwable?, croppedDocFile: File) {
        if(error != null) return

        route(DocEditingRoutes.DocEffects(croppedDocFile.toUri()))
    }


}