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

package com.paulrybitskyi.docskanner.ui.effects

import android.graphics.Bitmap
import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.domain.ClearAppCacheUseCase
import com.paulrybitskyi.docskanner.domain.CreatePdfDocumentUseCase
import com.paulrybitskyi.docskanner.ui.Constants
import com.paulrybitskyi.docskanner.ui.base.BaseViewModel
import com.paulrybitskyi.docskanner.ui.base.events.commons.GeneralCommands
import com.paulrybitskyi.docskanner.utils.PdfDocumentFileCreator
import com.paulrybitskyi.docskanner.utils.StringProvider
import com.paulrybitskyi.docskanner.utils.combine
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


private const val PARAM_DOC_FILE = "doc_file"


internal class DocEffectsViewModel @ViewModelInject constructor(
    private val createPdfDocumentUseCase: CreatePdfDocumentUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val pdfDocumentFileCreator: PdfDocumentFileCreator,
    private val stringProvider: StringProvider,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {


    private val _toolbarProgressBarVisibility = MutableLiveData(false)
    private val _saveButtonEnabledState = MutableLiveData(true)

    val toolbarProgressBarVisibility: LiveData<Boolean>
        get() = _toolbarProgressBarVisibility

    val saveButtonEnabledState: LiveData<Boolean>
        get() = _saveButtonEnabledState

    val docFile: LiveData<Uri> = liveData {
        delay(Constants.DEFAULT_WINDOW_ANIMATION_DURATION)
        emit(checkNotNull(savedStateHandle.get<Uri>(PARAM_DOC_FILE)))
    }


    fun onToolbarLeftButtonClicked() {
        route(DocEffectsRoutes.NavigateBack)
    }


    fun onMagicColorEffectClicked() {
        dispatchCommand(DocEffectsCommands.ApplyMagicColorEffect)
    }


    fun onGrayModeEffectClicked() {
        dispatchCommand(DocEffectsCommands.ApplyGrayModeEffect)
    }


    fun onBlackAndWhiteEffectClicked() {
        dispatchCommand(DocEffectsCommands.ApplyBlackAndWhiteEffect)
    }


    fun onClearButtonClicked() {
        dispatchCommand(DocEffectsCommands.ClearEffect)
    }


    fun onApplyingEffectStarted() {
        _toolbarProgressBarVisibility.value = true
    }


    fun onApplyingEffectFinished() {
        _toolbarProgressBarVisibility.value = false
    }


    fun onSaveButtonClicked(finalDocument: Bitmap) {
        showFileNameInputDialog(finalDocument)
    }


    private fun showFileNameInputDialog(finalDocument: Bitmap) {
        val defaultFileName = pdfDocumentFileCreator.createDefaultPdfFileName()
        val dialogConfig = DialogConfig(
            title = stringProvider.getString(R.string.doc_effects_file_name_dialog_title),
            inputHint = stringProvider.getString(R.string.doc_effects_file_name_dialog_hint),
            inputPrefill = defaultFileName,
            negativeBtnText = stringProvider.getString(R.string.action_cancel),
            positiveBtnText = stringProvider.getString(R.string.ok),
            inputCallback = { onFileNameEntered(it, finalDocument) }
        )

        dispatchCommand(DocEffectsCommands.ShowDialog(dialogConfig))
    }


    private fun onFileNameEntered(fileName: String, finalDocument: Bitmap) {
        if(fileName.isBlank()) {
            val message = stringProvider.getString(R.string.error_invalid_file_name)
            dispatchCommand(GeneralCommands.ShowLongToast(message))
        } else {
            runFinalizationFlow(fileName, finalDocument)
        }
    }


    private fun runFinalizationFlow(fileName: String, finalDocument: Bitmap) {
        viewModelScope.launch {
            combine(
                createPdfDocument(fileName, finalDocument),
                clearAppCache()
            )
            .onStart { onFinalizationFlowStarted() }
            .onCompletion { onFinalizationFlowFinished(it) }
            .catch {  } // Ignore for now
            .collect()
        }
    }


    private suspend fun createPdfDocument(fileName: String, finalDocument: Bitmap): Flow<Unit> {
        val pdfDocument = pdfDocumentFileCreator.createPdfFile(fileName)
        val params = CreatePdfDocumentUseCase.Params(
            bitmap = finalDocument,
            destinationFile = pdfDocument
        )

        return createPdfDocumentUseCase.execute(params)
            .catch {
                val message = stringProvider.getString(R.string.error_pdf_document_creation_failed)
                dispatchCommand(GeneralCommands.ShowLongToast(message))
                throw it
            }
    }


    private suspend fun clearAppCache(): Flow<Unit> {
        return clearAppCacheUseCase.execute(Unit)
    }


    private fun onFinalizationFlowStarted() {
        _toolbarProgressBarVisibility.value = true
        _saveButtonEnabledState.value = false
    }


    private fun onFinalizationFlowFinished(error: Throwable?) {
        if(error != null) {
            onFinalizationFlowFailed()
            return
        }

        dispatchCommand(GeneralCommands.ShowShortToast(stringProvider.getString(R.string.success)))
        route(DocEffectsRoutes.Dashboard)
    }


    private fun onFinalizationFlowFailed() {
        _toolbarProgressBarVisibility.value = false
        _saveButtonEnabledState.value = true
    }


}