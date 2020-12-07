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

package com.paulrybitskyi.docskanner.ui.scanning

import android.net.Uri
import androidx.fragment.app.viewModels
import com.paulrybitskyi.commons.ktx.onClick
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.databinding.FragmentDocScanningBinding
import com.paulrybitskyi.docskanner.ui.base.BaseFragment
import com.paulrybitskyi.docskanner.ui.base.events.Command
import com.paulrybitskyi.docskanner.ui.base.events.Route
import com.paulrybitskyi.docskanner.utils.utils.navController
import com.paulrybitskyi.docskanner.utils.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class DocScanningFragment : BaseFragment<
    FragmentDocScanningBinding,
    DocScanningViewModel
>(R.layout.fragment_doc_scanning) {


    override val viewBinding by viewBinding(FragmentDocScanningBinding::bind)
    override val viewModel by viewModels<DocScanningViewModel>()


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
        confirmBtnTv.onClick { cropperView.cropBitmap(viewModel::onConfirmButtonClicked) }
    }


    override fun onBindViewModel() = with(viewModel) {
        super.onBindViewModel()

        observeDocFile()
    }


    private fun DocScanningViewModel.observeDocFile() {
        docFile.observe(viewLifecycleOwner) {
            viewBinding.cropperView.imageUri = it
        }
    }


    override fun onHandleCommand(command: Command) {
        super.onHandleCommand(command)

        when(command) {
            is DocScanningCommands.RotateImageLeft -> rotateImageLeft()
            is DocScanningCommands.RotateImageRight -> rotateImageRight()
        }
    }


    private fun rotateImageLeft() {
        viewBinding.cropperView.rotateLeft()
    }


    private fun rotateImageRight() {
        viewBinding.cropperView.rotateRight()
    }


    override fun onRoute(route: Route) {
        super.onRoute(route)

        when(route) {
            is DocScanningRoutes.DocEffects -> navigateToDocEffectsScreen(route.docFile)
            is DocScanningRoutes.NavigateBack -> navigateBack()
        }
    }


    private fun navigateToDocEffectsScreen(docFile: Uri) {
        navController.navigate(DocScanningFragmentDirections.actionDocEffectsFragment(docFile))
    }


    private fun navigateBack() {
        navController.popBackStack()
    }


}