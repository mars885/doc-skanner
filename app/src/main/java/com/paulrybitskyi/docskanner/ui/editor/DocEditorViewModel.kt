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

package com.paulrybitskyi.docskanner.ui.editor

import android.graphics.Bitmap
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.core.factories.PdfDocumentFileNameFactory
import com.paulrybitskyi.docskanner.core.providers.StringProvider
import com.paulrybitskyi.docskanner.core.utils.combine
import com.paulrybitskyi.docskanner.core.utils.onEachError
import com.paulrybitskyi.docskanner.core.utils.onError
import com.paulrybitskyi.docskanner.core.utils.onSuccess
import com.paulrybitskyi.docskanner.core.verifiers.FileExtension
import com.paulrybitskyi.docskanner.core.verifiers.FileNameExtensionVerifier
import com.paulrybitskyi.docskanner.domain.ClearAppCacheUseCase
import com.paulrybitskyi.docskanner.domain.ConvertImageToPdfUseCase
import com.paulrybitskyi.docskanner.ui.Constants
import com.paulrybitskyi.docskanner.ui.base.BaseViewModel
import com.paulrybitskyi.docskanner.ui.base.events.commons.GeneralCommand
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import com.paulrybitskyi.docskanner.utils.dialogs.DialogContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File


private const val PARAM_DOC_IMAGE_FILE = "doc_image_file"


internal class DocEditorViewModel @ViewModelInject constructor(
    private val convertImageToPdfUseCase: ConvertImageToPdfUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val pdfDocumentFileNameFactory: PdfDocumentFileNameFactory,
    private val fileNameExtensionVerifier: FileNameExtensionVerifier,
    private val stringProvider: StringProvider,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {


    private val _toolbarProgressBarVisibility = MutableLiveData(false)
    private val _saveButtonEnabledState = MutableLiveData(true)

    val toolbarProgressBarVisibility: LiveData<Boolean>
        get() = _toolbarProgressBarVisibility

    val saveButtonEnabledState: LiveData<Boolean>
        get() = _saveButtonEnabledState

    val docImageFile: LiveData<File> = liveData {
        delay(Constants.DEFAULT_WINDOW_ANIMATION_DURATION)
        emit(checkNotNull(savedStateHandle.get<File>(PARAM_DOC_IMAGE_FILE)))
    }


    fun onToolbarLeftButtonClicked() {
        route(DocEditorRoute.NavigateBack)
    }


    fun onGrayEffectClicked() {
        dispatchCommand(DocEditorCommand.ApplyGrayEffect)
    }


    fun onFirstBawEffectClicked() {
        dispatchCommand(DocEditorCommand.ApplyFirstBawEffect)
    }


    fun onSecondBawEffectClicked() {
        dispatchCommand(DocEditorCommand.ApplySecondBawEffect)
    }


    fun onClearButtonClicked() {
        dispatchCommand(DocEditorCommand.ClearEffect)
    }


    fun onApplyingEffectStarted() {
        _toolbarProgressBarVisibility.value = true
    }


    fun onApplyingEffectFinished() {
        _toolbarProgressBarVisibility.value = false
    }


    fun onSaveButtonClicked(documentImage: Bitmap) {
        showFileNameInputDialog(documentImage)
    }


    private fun showFileNameInputDialog(documentImage: Bitmap) {
        val dialogContent = DialogContent.Input(
            hint = stringProvider.getString(R.string.doc_editor_file_name_dialog_hint),
            prefill = pdfDocumentFileNameFactory.createFileName(),
            callback = { onFileNameEntered(it, documentImage) }
        )
        val dialogConfig = DialogConfig(
            content = dialogContent,
            title = stringProvider.getString(R.string.doc_editor_file_name_dialog_title),
            negativeBtnText = stringProvider.getString(R.string.action_cancel),
            positiveBtnText = stringProvider.getString(R.string.ok)
        )

        dispatchCommand(DocEditorCommand.ShowDialog(dialogConfig))
    }


    private fun onFileNameEntered(fileName: String, documentImage: Bitmap) {
        if(fileNameExtensionVerifier.hasExtension(fileName, FileExtension.PDF)) {
            runFinalizationFlow(fileName, documentImage)
        } else {
            val message = stringProvider.getString(R.string.error_invalid_doc_file_name)
            dispatchCommand(GeneralCommand.ShowLongToast(message))
        }
    }


    private fun runFinalizationFlow(fileName: String, documentImage: Bitmap) {
        viewModelScope.launch {
            combine(
                convertImageToPdf(fileName, documentImage),
                clearAppCache()
            )
            .onStart { onFinalizationFlowStarted() }
            .onSuccess { onFinalizationFlowFinished() }
            .onError { onFinalizationFlowFailed() }
            .collect()
        }
    }


    private suspend fun convertImageToPdf(fileName: String, documentImage: Bitmap): Flow<Unit> {
        val params = ConvertImageToPdfUseCase.Params(documentImage, fileName)

        return convertImageToPdfUseCase.execute(params)
            .onEachError {
                val message = stringProvider.getString(R.string.error_pdf_document_creation_failed)
                dispatchCommand(GeneralCommand.ShowLongToast(message))
            }
    }


    private suspend fun clearAppCache(): Flow<Unit> {
        return clearAppCacheUseCase.execute(Unit)
    }


    private fun onFinalizationFlowStarted() {
        _toolbarProgressBarVisibility.value = true
        _saveButtonEnabledState.value = false
    }


    private fun onFinalizationFlowFinished() {
        dispatchCommand(GeneralCommand.ShowShortToast(stringProvider.getString(R.string.success)))
        route(DocEditorRoute.Dashboard)
    }


    private fun onFinalizationFlowFailed() {
        _toolbarProgressBarVisibility.value = false
        _saveButtonEnabledState.value = true
    }


}