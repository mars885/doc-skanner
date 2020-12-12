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

package com.paulrybitskyi.docskanner.ui.dashboard.fragment

import android.Manifest.permission.CAMERA
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.core.*
import com.paulrybitskyi.docskanner.domain.ObserveAppStorageFolderFilesUseCase
import com.paulrybitskyi.docskanner.ui.base.BaseViewModel
import com.paulrybitskyi.docskanner.ui.base.events.commons.GeneralCommands
import com.paulrybitskyi.docskanner.ui.dashboard.fragment.mapping.DocsUiStateFactory
import com.paulrybitskyi.docskanner.ui.views.docs.DocModel
import com.paulrybitskyi.docskanner.ui.views.docs.DocsUiState
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import com.paulrybitskyi.docskanner.utils.dialogs.DialogItem
import com.paulrybitskyi.docskanner.utils.dialogs.DialogContent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


internal class DashboardViewModel @ViewModelInject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val observeAppStorageFolderFilesUseCase: ObserveAppStorageFolderFilesUseCase,
    private val docsUiStateFactory: DocsUiStateFactory,
    private val dispatcherProvider: DispatcherProvider,
    private val stringProvider: StringProvider,
    private val cameraPresenceVerifier: CameraPresenceVerifier,
    private val permissionVerifier: PermissionVerifier,
    private val temporaryImageFileCreator: TemporaryImageFileCreator,
    private val shareableUriFactory: ShareableUriFactory
) : BaseViewModel() {


    private var isLoadingData = false

    private var cameraImageUri: Uri? = null

    private val _toolbarProgressBarVisibility = MutableLiveData(false)
    private val _uiState = MutableLiveData<DocsUiState>(DocsUiState.Empty)

    val toolbarProgressBarVisibility: LiveData<Boolean>
        get() = _toolbarProgressBarVisibility

    val uiState: LiveData<DocsUiState>
        get() = _uiState


    fun loadData() {
        if(isLoadingData) return

        viewModelScope.launch {
            loadDataInternal()
        }
    }


    private suspend fun loadDataInternal() {
        observeAppStorageFolderFilesUseCase.execute(Unit)
            .map(docsUiStateFactory::createWithResultState)
            .flowOn(dispatcherProvider.computation)
            .onStart {
                isLoadingData = true
                emit(docsUiStateFactory.createWithLoadingState())
            }
            .onCompletion { isLoadingData = false }
            .catch {
                val errorMessage = stringProvider.getString(R.string.error_unknown_message)

                dispatchCommand(GeneralCommands.ShowShortToast(errorMessage))
                emit(docsUiStateFactory.createWithEmptyState())
            }
            .collect(_uiState::setValue)
    }


    fun onDocClicked(model: DocModel) {
        route(DashboardRoutes.DocPreview(model.filePath))
    }


    fun onScanButtonClicked() {
        showImagePickerDialog()
    }


    private fun showImagePickerDialog() {
        dispatchCommand(DashboardCommands.ShowDialog(constructImagePickerDialogConfig()))
    }


    private fun constructImagePickerDialogConfig(): DialogConfig {
        val options = buildList {
            if(cameraPresenceVerifier.isCameraPresent()) {
                add(ImagePickerOption.CAMERA.toDialogItem())
            }

            add(ImagePickerOption.GALLERY.toDialogItem())
        }

        val dialogConfig = DialogConfig(
            content = DialogContent.List(options, callback = ::onImagePickerItemSelected),
            title = stringProvider.getString(R.string.doc_preview_image_picker_dialog_title)
        )

        return dialogConfig
    }


    private fun onImagePickerItemSelected(item: DialogItem) {
        when(item.tag as ImagePickerOption) {
            ImagePickerOption.CAMERA -> onCameraOptionSelected()
            ImagePickerOption.GALLERY -> onGalleryOptionSelected()
        }
    }


    private fun onCameraOptionSelected() {
        if(permissionVerifier.isPermissionGranted(CAMERA)) {
            takeCameraImage()
            return
        }

        dispatchCommand(DashboardCommands.RequestCameraPermission)
    }


    private fun takeCameraImage() {
        val imageFile = temporaryImageFileCreator.createTempImageFile()
        val imageUri = shareableUriFactory.createShareableUri(imageFile)
            .also { cameraImageUri = it }

        dispatchCommand(DashboardCommands.TakeCameraImage(imageUri))
    }


    fun onCameraPermissionGranted() {
        takeCameraImage()
    }


    fun onCameraPermissionDenied() {
        val message = stringProvider.getString(R.string.error_camera_permission_not_granted)
        val dialogConfig = DialogConfig(
            content = DialogContent.Info(message),
            title = stringProvider.getString(R.string.error),
            positiveBtnText = stringProvider.getString(R.string.ok)
        )

        dispatchCommand(DashboardCommands.ShowDialog(dialogConfig))
    }


    fun onCameraImageTaken() {
        route(DashboardRoutes.DocScanning(checkNotNull(cameraImageUri)))
    }


    private fun onGalleryOptionSelected() {
        dispatchCommand(DashboardCommands.PickGalleryImage)
    }


    fun onGalleryImagePicked(imageUri: Uri) {
        _toolbarProgressBarVisibility.value = true

        viewModelScope.launch(dispatcherProvider.io) {
            copyGalleryImage(imageUri)
        }
    }


    private suspend fun copyGalleryImage(galleryImageUri: Uri) {
        val contentResolver = applicationContext.contentResolver
        val sourceImageIs = contentResolver.openInputStream(galleryImageUri)
        val destImageFile = temporaryImageFileCreator.createTempImageFile()
        val destImageOs = destImageFile.outputStream()

        sourceImageIs?.copyTo(destImageOs)

        withContext(dispatcherProvider.main) {
            _toolbarProgressBarVisibility.value = false
            route(DashboardRoutes.DocScanning(destImageFile.toUri()))
        }
    }


    private fun ImagePickerOption.toDialogItem(): DialogItem {
        val title = stringProvider.getString(
            when(this) {
                ImagePickerOption.CAMERA -> R.string.camera
                ImagePickerOption.GALLERY -> R.string.gallery
            }
        )

        return DialogItem(title = title, tag = this)
    }


}


private enum class ImagePickerOption {

    CAMERA,
    GALLERY

}