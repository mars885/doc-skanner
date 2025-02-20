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

package com.paulrybitskyi.docskanner.ui.dashboard.fragment

import android.Manifest.permission.CAMERA
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.core.factories.ShareableUriFactory
import com.paulrybitskyi.docskanner.core.factories.TemporaryImageFileFactory
import com.paulrybitskyi.docskanner.core.providers.DispatcherProvider
import com.paulrybitskyi.docskanner.core.providers.StringProvider
import com.paulrybitskyi.docskanner.core.utils.onError
import com.paulrybitskyi.docskanner.core.utils.onSuccess
import com.paulrybitskyi.docskanner.core.verifiers.CameraPresenceVerifier
import com.paulrybitskyi.docskanner.core.verifiers.PermissionVerifier
import com.paulrybitskyi.docskanner.domain.CopyFileUseCase
import com.paulrybitskyi.docskanner.domain.ObserveAppStorageFolderFilesUseCase
import com.paulrybitskyi.docskanner.ui.base.BaseViewModel
import com.paulrybitskyi.docskanner.ui.base.events.commons.GeneralCommand
import com.paulrybitskyi.docskanner.ui.dashboard.fragment.mapping.DocsUiStateFactory
import com.paulrybitskyi.docskanner.ui.views.docs.DocModel
import com.paulrybitskyi.docskanner.ui.views.docs.DocsUiState
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import com.paulrybitskyi.docskanner.utils.dialogs.DialogContent
import com.paulrybitskyi.docskanner.utils.dialogs.DialogItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
internal class DashboardViewModel @Inject constructor(
    private val observeAppStorageFolderFilesUseCase: ObserveAppStorageFolderFilesUseCase,
    private val copyFileUseCase: CopyFileUseCase,
    private val docsUiStateFactory: DocsUiStateFactory,
    private val dispatcherProvider: DispatcherProvider,
    private val stringProvider: StringProvider,
    private val cameraPresenceVerifier: CameraPresenceVerifier,
    private val permissionVerifier: PermissionVerifier,
    private val temporaryImageFileFactory: TemporaryImageFileFactory,
    private val shareableUriFactory: ShareableUriFactory
) : BaseViewModel() {


    private var isLoadingData = false

    private var cameraImageFile: File? = null

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
            .onError {
                val errorMessage = stringProvider.getString(R.string.error_unknown_message)

                dispatchCommand(GeneralCommand.ShowShortToast(errorMessage))
                emit(docsUiStateFactory.createWithEmptyState())
            }
            .collect(_uiState::setValue)
    }


    fun onDocClicked(model: DocModel) {
        route(DashboardRoute.DocPreview(File(model.filePath)))
    }


    fun onScanButtonClicked() {
        showImagePickerDialog()
    }


    private fun showImagePickerDialog() {
        dispatchCommand(DashboardCommand.ShowDialog(constructImagePickerDialogConfig()))
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

        dispatchCommand(DashboardCommand.RequestCameraPermission)
    }


    private fun takeCameraImage() {
        val imageFile = temporaryImageFileFactory.createTempImageFile()
        val imageUri = shareableUriFactory.createShareableUri(imageFile)

        cameraImageFile = imageFile

        dispatchCommand(DashboardCommand.TakeCameraImage(imageUri))
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

        dispatchCommand(DashboardCommand.ShowDialog(dialogConfig))
    }


    fun onCameraImageTaken() {
        route(DashboardRoute.DocScanner(checkNotNull(cameraImageFile)))
    }


    private fun onGalleryOptionSelected() {
        dispatchCommand(DashboardCommand.PickGalleryImage)
    }


    fun onGalleryImagePicked(imageUri: Uri) {
        viewModelScope.launch {
            copyGalleryImage(imageUri)
        }
    }


    private suspend fun copyGalleryImage(imageUri: Uri) {
        val galleryImageCopy = temporaryImageFileFactory.createTempImageFile()
        val useCaseParams = CopyFileUseCase.Params(
            source = imageUri,
            destination = galleryImageCopy.toUri()
        )

        copyFileUseCase.execute(useCaseParams)
            .onStart { showToolbarProgressBar() }
            .onSuccess {
                hideToolbarProgressBar()
                route(DashboardRoute.DocScanner(galleryImageCopy))
            }
            .onError {
                hideToolbarProgressBar()

                val text = stringProvider.getString(R.string.error_cannot_process_gallery_image)
                dispatchCommand(GeneralCommand.ShowLongToast(text))
            }
            .collect()
    }


    private fun showToolbarProgressBar() {
        _toolbarProgressBarVisibility.value = true
    }


    private fun hideToolbarProgressBar() {
        _toolbarProgressBarVisibility.value = false
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