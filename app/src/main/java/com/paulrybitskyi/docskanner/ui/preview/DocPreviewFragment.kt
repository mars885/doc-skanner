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

package com.paulrybitskyi.docskanner.ui.preview

import androidx.fragment.app.viewModels
import com.paulrybitskyi.commons.ktx.getDimensionPixelSize
import com.paulrybitskyi.commons.ktx.makeGone
import com.paulrybitskyi.commons.ktx.makeVisible
import com.paulrybitskyi.commons.ktx.removeElevation
import com.paulrybitskyi.commons.navigation.navController
import com.paulrybitskyi.commons.utils.viewBinding
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.databinding.FragmentDocPreviewBinding
import com.paulrybitskyi.docskanner.ui.base.BaseFragment
import com.paulrybitskyi.docskanner.ui.base.events.Route
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class DocPreviewFragment : BaseFragment<
    FragmentDocPreviewBinding,
    DocPreviewViewModel
>(R.layout.fragment_doc_preview) {


    override val viewBinding by viewBinding(FragmentDocPreviewBinding::bind)
    override val viewModel by viewModels<DocPreviewViewModel>()


    override fun onInit() {
        super.onInit()

        initToolbar()
    }


    private fun initToolbar() = with(viewBinding.toolbar) {
        removeElevation()
        onLeftButtonClickListener = { viewModel.onToolbarLeftButtonClicked() }
    }


    override fun onBindViewModel() = with(viewModel) {
        super.onBindViewModel()

        observeToolbarTitle()
        observeDocFile()
    }


    private fun DocPreviewViewModel.observeToolbarTitle() {
        toolbarTitle.observe(viewLifecycleOwner) {
            viewBinding.toolbar.titleText = it
        }
    }


    private fun DocPreviewViewModel.observeDocFile() {
        docFile.observe(viewLifecycleOwner) {
            viewBinding.pdfView.fromFile(it)
                .spacing(getDimensionPixelSize(R.dimen.doc_preview_page_spacing))
                .onRender { _, _, _ -> onPdfRendered() }
                .onError(::onError)
                .load()
        }
    }


    private fun onPdfRendered() {
        viewBinding.progressBar.makeGone()
    }


    private fun onError(error: Throwable) = with(viewBinding) {
        progressBar.makeGone()
        pdfView.makeGone()
        infoView.makeVisible()
    }


    override fun onRoute(route: Route) {
        super.onRoute(route)

        when(route) {
            is DocPreviewRoute.NavigateBack -> navigateBack()
        }
    }


    private fun navigateBack() {
        navController.popBackStack()
    }


}