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

package com.paulrybitskyi.docscanner.ui.splash

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docscanner.R
import com.paulrybitskyi.docscanner.domain.CreateAppStorageFolderUseCase
import com.paulrybitskyi.docscanner.ui.base.BaseViewModel
import com.paulrybitskyi.docscanner.ui.base.events.commons.GeneralCommands
import com.paulrybitskyi.docscanner.utils.PermissionVerifier
import com.paulrybitskyi.docscanner.utils.StringProvider
import com.paulrybitskyi.docscanner.utils.dialogs.DialogConfig
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

internal class SplashViewModel @ViewModelInject constructor(
    private val createAppStorageFolderUseCase: CreateAppStorageFolderUseCase,
    private val permissionVerifier: PermissionVerifier,
    private val stringProvider: StringProvider
) : BaseViewModel() {


    fun init() {
        if(isStoragePermissionGranted()) {
            onStoragePermissionGranted()
            return
        }

        dispatchCommand(SplashCommands.RequestStoragePermission)
    }


    private fun isStoragePermissionGranted(): Boolean {
        return permissionVerifier.isPermissionGranted(WRITE_EXTERNAL_STORAGE)
    }


    fun onStoragePermissionGranted() {
        viewModelScope.launch {
            createAppStorageFolder()
        }
    }


    private suspend fun createAppStorageFolder() {
        createAppStorageFolderUseCase.execute(Unit)
            .onCompletion { onAppStorageFolderCreationCompleted(it) }
            .catch { onAppStorageFolderCreationFailed() }
            .collect()
    }


    private fun onAppStorageFolderCreationFailed() {
        val message = stringProvider.getString(R.string.error_storage_folder_creation_failed)

        dispatchCommand(GeneralCommands.ShowLongToast(message))
        exit()
    }


    private fun onAppStorageFolderCreationCompleted(error: Throwable?) {
        if(error != null) return

        route(SplashRoutes.Dashboard)
    }


    fun onStoragePermissionDenied() {
        val dialogConfig = DialogConfig(
            title = stringProvider.getString(R.string.error),
            content = stringProvider.getString(R.string.error_storage_permission_not_granted),
            positiveBtnText = stringProvider.getString(R.string.ok),
            onDismiss = ::exit
        )

        dispatchCommand(SplashCommands.ShowStoragePermissionDeniedDialog(dialogConfig))
    }


    private fun exit() {
        route(SplashRoutes.Exit)
    }


}