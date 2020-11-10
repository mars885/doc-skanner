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

package com.paulrybitskyi.docscanner.ui.dashboard.fragment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paulrybitskyi.docscanner.R
import com.paulrybitskyi.docscanner.domain.ObserveAppStorageFolderFilesUseCase
import com.paulrybitskyi.docscanner.ui.base.BaseViewModel
import com.paulrybitskyi.docscanner.ui.base.events.commons.GeneralCommands
import com.paulrybitskyi.docscanner.ui.dashboard.fragment.mapping.DocsUiStateFactory
import com.paulrybitskyi.docscanner.ui.views.DocModel
import com.paulrybitskyi.docscanner.ui.views.DocsUiState
import com.paulrybitskyi.docscanner.utils.DispatcherProvider
import com.paulrybitskyi.docscanner.utils.StringProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class DashboardViewModel @ViewModelInject constructor(
    private val observeAppStorageFolderFilesUseCase: ObserveAppStorageFolderFilesUseCase,
    private val docsUiStateFactory: DocsUiStateFactory,
    private val dispatcherProvider: DispatcherProvider,
    private val stringProvider: StringProvider
) : BaseViewModel() {


    private var isLoadingData = false

    private val _uiState = MutableLiveData<DocsUiState>(DocsUiState.Empty)

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
        //todo
    }


}