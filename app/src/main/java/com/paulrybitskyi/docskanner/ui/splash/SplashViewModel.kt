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

package com.paulrybitskyi.docskanner.ui.splash

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.core.providers.StringProvider
import com.paulrybitskyi.docskanner.core.utils.combine
import com.paulrybitskyi.docskanner.core.utils.onError
import com.paulrybitskyi.docskanner.core.utils.onSuccess
import com.paulrybitskyi.docskanner.core.verifiers.PermissionVerifier
import com.paulrybitskyi.docskanner.domain.ClearAppCacheUseCase
import com.paulrybitskyi.docskanner.domain.CreateAppStorageFolderUseCase
import com.paulrybitskyi.docskanner.imageprocessing.ImageProcessorInitializer
import com.paulrybitskyi.docskanner.ui.base.BaseViewModel
import com.paulrybitskyi.docskanner.ui.base.events.commons.GeneralCommand
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import com.paulrybitskyi.docskanner.utils.dialogs.DialogContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SplashViewModel @Inject constructor(
    private val imageProcessorInitializer: ImageProcessorInitializer,
    private val createAppStorageFolderUseCase: CreateAppStorageFolderUseCase,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
    private val permissionVerifier: PermissionVerifier,
    private val stringProvider: StringProvider
) : BaseViewModel() {


    fun init() {
        if(isStoragePermissionGranted()) {
            onStoragePermissionGranted()
            return
        }

        dispatchCommand(SplashCommand.RequestStoragePermission)
    }


    private fun isStoragePermissionGranted(): Boolean {
        return permissionVerifier.isPermissionGranted(WRITE_EXTERNAL_STORAGE)
    }


    fun onStoragePermissionDenied() {
        val message = stringProvider.getString(R.string.error_storage_permission_not_granted)
        val dialogConfig = DialogConfig(
            content = DialogContent.Info(message),
            title = stringProvider.getString(R.string.error),
            positiveBtnText = stringProvider.getString(R.string.ok),
            isCancelable = false,
            onDismiss = ::exit
        )

        dispatchCommand(SplashCommand.ShowDialog(dialogConfig))
    }


    fun onStoragePermissionGranted() {
        runInitializationFlow()
    }


    private fun runInitializationFlow() {
        viewModelScope.launch {
            combine(
                initImageProcessor(),
                createAppStorageFolder(),
                clearAppCache()
            )
            .onSuccess { onInitializationFlowSucceeded() }
            .onError { onInitializationFlowFailed() }
            .collect()
        }
    }


    private fun initImageProcessor(): Flow<Unit> {
        return flow {
            imageProcessorInitializer.init()
        }
    }


    private suspend fun createAppStorageFolder(): Flow<Unit> {
        return createAppStorageFolderUseCase.execute(Unit)
    }


    private suspend fun clearAppCache(): Flow<Unit> {
        return clearAppCacheUseCase.execute(Unit)
    }


    private fun onInitializationFlowSucceeded() {
        route(SplashRoute.Dashboard)
    }


    private fun onInitializationFlowFailed() {
        val message = stringProvider.getString(R.string.error_initialization_failed)

        dispatchCommand(GeneralCommand.ShowLongToast(message))
        exit()
    }


    private fun exit() {
        route(SplashRoute.Exit)
    }


}