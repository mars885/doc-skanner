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

import androidx.fragment.app.viewModels
import com.paulrybitskyi.commons.ktx.onClick
import com.paulrybitskyi.commons.navigation.navController
import com.paulrybitskyi.commons.utils.viewBinding
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.databinding.FragmentDocScannerBinding
import com.paulrybitskyi.docskanner.ui.base.BaseFragment
import com.paulrybitskyi.docskanner.ui.base.events.Command
import com.paulrybitskyi.docskanner.ui.base.events.Route
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
internal class DocScannerFragment : BaseFragment<
    FragmentDocScannerBinding,
    DocScannerViewModel
>(R.layout.fragment_doc_scanner) {


    override val viewBinding by viewBinding(FragmentDocScannerBinding::bind)
    override val viewModel by viewModels<DocScannerViewModel>()


    override fun onInit() {
        super.onInit()

        initToolbar()
        initButtons()
    }


    private fun initToolbar() = with(viewBinding.toolbar) {
        onLeftButtonClickListener = { viewModel.onToolbarLeftButtonClicked() }
    }


    private fun initButtons() = with(viewBinding) {
        rotateLeftBtnTv.onClick { viewModel.onRotateLeftButtonClicked() }
        rotateRightBtnTv.onClick { viewModel.onRotateRightButtonClicked() }
        confirmBtnTv.onClick { scannerView.scanDocument(viewModel::onConfirmButtonClicked) }
    }


    override fun onBindViewModel() = with(viewModel) {
        super.onBindViewModel()

        observeDocImageFile()
    }


    private fun DocScannerViewModel.observeDocImageFile() {
        docImageFile.observe(viewLifecycleOwner) {
            viewBinding.scannerView.imageFile = it
        }
    }


    override fun onHandleCommand(command: Command) {
        super.onHandleCommand(command)

        when(command) {
            is DocScannerCommand.RotateImageLeft -> viewBinding.scannerView.rotateLeft()
            is DocScannerCommand.RotateImageRight -> viewBinding.scannerView.rotateRight()
        }
    }


    override fun onRoute(route: Route) {
        super.onRoute(route)

        when(route) {
            is DocScannerRoute.DocEditor -> navigateToDocEditorScreen(route.docImageFile)
            is DocScannerRoute.NavigateBack -> navigateBack()
        }
    }


    private fun navigateToDocEditorScreen(docImageFile: File) {
        navController.navigate(DocScannerFragmentDirections.actionDocEditorFragment(docImageFile))
    }


    private fun navigateBack() {
        navController.popBackStack()
    }


}