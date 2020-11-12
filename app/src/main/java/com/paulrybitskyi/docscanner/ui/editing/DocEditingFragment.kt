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

package com.paulrybitskyi.docscanner.ui.editing

import android.net.Uri
import androidx.fragment.app.viewModels
import com.paulrybitskyi.commons.ktx.onClick
import com.paulrybitskyi.docscanner.R
import com.paulrybitskyi.docscanner.databinding.FragmentDocEditingBinding
import com.paulrybitskyi.docscanner.ui.base.BaseFragment
import com.paulrybitskyi.docscanner.ui.base.events.Command
import com.paulrybitskyi.docscanner.ui.base.events.Route
import com.paulrybitskyi.docscanner.utils.extensions.navController
import com.paulrybitskyi.docscanner.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class DocEditingFragment : BaseFragment<
    FragmentDocEditingBinding,
    DocEditingViewModel
>(R.layout.fragment_doc_editing) {


    override val viewBinding by viewBinding(FragmentDocEditingBinding::bind)
    override val viewModel by viewModels<DocEditingViewModel>()


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
        nextBtnTv.onClick { cropperView.cropBitmap(viewModel::onNextButtonClicked) }
    }


    override fun onBindViewModel() = with(viewModel) {
        super.onBindViewModel()

        observeDocFile()
    }


    private fun DocEditingViewModel.observeDocFile() {
        docFile.observe(viewLifecycleOwner) {
            viewBinding.cropperView.imageUri = it
        }
    }


    override fun onHandleCommand(command: Command) {
        super.onHandleCommand(command)

        when(command) {
            is DocEditingCommands.RotateImageLeft -> rotateImageLeft()
            is DocEditingCommands.RotateImageRight -> rotateImageRight()
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
            is DocEditingRoutes.DocEffects -> navigateToDocEffectsScreen(route.docFile)
            is DocEditingRoutes.NavigateBack -> navigateBack()
        }
    }


    private fun navigateToDocEffectsScreen(docFile: Uri) {
        navController.navigate(DocEditingFragmentDirections.actionDocEffectsFragment(docFile))
    }


    private fun navigateBack() {
        navController.popBackStack()
    }


}