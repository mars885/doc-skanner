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

package com.paulrybitskyi.docskanner.ui.splash

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.domain.ClearAppCacheUseCase
import com.paulrybitskyi.docskanner.domain.CreateAppStorageFolderUseCase
import com.paulrybitskyi.docskanner.domain.InitOpenCvLibraryUseCase
import com.paulrybitskyi.docskanner.ui.base.BaseViewModel
import com.paulrybitskyi.docskanner.ui.base.events.commons.GeneralCommands
import com.paulrybitskyi.docskanner.utils.PermissionVerifier
import com.paulrybitskyi.docskanner.utils.StringProvider
import com.paulrybitskyi.docskanner.utils.combine
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.io.File

internal class SplashViewModel @ViewModelInject constructor(
    private val initOpenCvLibraryUseCase: InitOpenCvLibraryUseCase,
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

        dispatchCommand(SplashCommands.RequestStoragePermission)
    }


    private fun isStoragePermissionGranted(): Boolean {
        return permissionVerifier.isPermissionGranted(WRITE_EXTERNAL_STORAGE)
    }


    fun onStoragePermissionDenied() {
        val dialogConfig = DialogConfig(
            title = stringProvider.getString(R.string.error),
            content = stringProvider.getString(R.string.error_storage_permission_not_granted),
            positiveBtnText = stringProvider.getString(R.string.ok),
            isCancelable = false,
            onDismiss = ::exit
        )

        dispatchCommand(SplashCommands.ShowDialog(dialogConfig))
    }


    fun onStoragePermissionGranted() {
        runInitializationFlow()
    }


    private fun runInitializationFlow() {
        viewModelScope.launch {
            combine(
                initOpenCvLibrary(),
                createAppStorageFolder(),
                clearAppCache()
            )
            .onCompletion { onInitializationFlowCompleted(it) }
            .catch { onInitializationFlowFailed() }
            .collect()
        }
    }


    private suspend fun initOpenCvLibrary(): Flow<Unit> {
        return initOpenCvLibraryUseCase.execute(Unit)
    }


    private suspend fun createAppStorageFolder(): Flow<File> {
        return createAppStorageFolderUseCase.execute(Unit)
    }


    private suspend fun clearAppCache(): Flow<Unit> {
        return clearAppCacheUseCase.execute(Unit)
    }


    private fun onInitializationFlowFailed() {
        val message = stringProvider.getString(R.string.error_initialization_failed)

        dispatchCommand(GeneralCommands.ShowLongToast(message))
        exit()
    }


    private fun onInitializationFlowCompleted(error: Throwable?) {
        if(error != null) return

        route(SplashRoutes.Dashboard)
    }


    private fun exit() {
        route(SplashRoutes.Exit)
    }


}