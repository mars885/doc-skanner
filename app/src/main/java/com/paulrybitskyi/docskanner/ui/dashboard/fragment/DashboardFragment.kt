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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.karumi.dexter.Dexter
import com.paulrybitskyi.commons.ktx.onClick
import com.paulrybitskyi.commons.navigation.navController
import com.paulrybitskyi.commons.utils.viewBinding
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.databinding.FragmentDashboardBinding
import com.paulrybitskyi.docskanner.ui.base.BaseFragment
import com.paulrybitskyi.docskanner.ui.base.events.Command
import com.paulrybitskyi.docskanner.ui.base.events.Route
import com.paulrybitskyi.docskanner.utils.dialogs.Dialog
import com.paulrybitskyi.docskanner.utils.dialogs.DialogBuilder
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import com.paulrybitskyi.docskanner.utils.dialogs.show
import com.paulrybitskyi.docskanner.core.utils.withListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val REQUEST_CODE_CAMERA = 1000
private const val REQUEST_CODE_GALLERY = 1001


@AndroidEntryPoint
internal class DashboardFragment : BaseFragment<
    FragmentDashboardBinding,
    DashboardViewModel
>(R.layout.fragment_dashboard) {


    override val viewBinding by viewBinding(FragmentDashboardBinding::bind)
    override val viewModel by viewModels<DashboardViewModel>()

    @Inject lateinit var dialogBuilder: DialogBuilder

    private var dialog: Dialog? = null


    override fun onInit() {
        super.onInit()

        initDocsView()
        initScanButton()
    }


    private fun initDocsView() = with(viewBinding.docsView) {
        onDocClickListener = viewModel::onDocClicked
    }


    private fun initScanButton() {
        viewBinding.scanBtn.onClick {
            viewModel.onScanButtonClicked()
        }
    }


    override fun onLoadData() {
        super.onLoadData()

        viewModel.loadData()
    }


    override fun onBindViewModel() = with(viewModel) {
        super.onBindViewModel()

        observeToolbarVisibility()
        observeDocsUiState()
    }


    private fun DashboardViewModel.observeToolbarVisibility() {
        toolbarProgressBarVisibility.observe(viewLifecycleOwner) {
            viewBinding.toolbarPb.isVisible = it
        }
    }


    private fun DashboardViewModel.observeDocsUiState() {
        uiState.observe(viewLifecycleOwner) {
            viewBinding.docsView.uiState = it
        }
    }


    override fun onHandleCommand(command: Command) {
        super.onHandleCommand(command)

        when(command) {
            is DashboardCommands.ShowDialog -> showDialog(command.config)
            is DashboardCommands.RequestCameraPermission -> requestCameraPermission()
            is DashboardCommands.TakeCameraImage -> takeCameraImage(command.destinationUri)
            is DashboardCommands.PickGalleryImage -> pickGalleryImage()
        }
    }


    private fun showDialog(config: DialogConfig) {
        dialog?.dismiss()
        dialog = dialogBuilder.buildDialog(requireContext(), config).show(viewLifecycleOwner)
    }


    private fun requestCameraPermission() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.CAMERA)
            .withListener(
                onPermissionGranted = { viewModel.onCameraPermissionGranted() },
                onPermissionDenied = { viewModel.onCameraPermissionDenied() }
            )
            .check()
    }


    private fun takeCameraImage(destinationUri: Uri) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .apply { putExtra(MediaStore.EXTRA_OUTPUT, destinationUri) }

        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }


    private fun pickGalleryImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }

        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if(resultCode != Activity.RESULT_OK) return

        when(requestCode) {
            REQUEST_CODE_CAMERA -> viewModel.onCameraImageTaken()
            REQUEST_CODE_GALLERY -> viewModel.onGalleryImagePicked(checkNotNull(intent?.data))
        }
    }


    override fun onRoute(route: Route) {
        super.onRoute(route)

        when(route) {
            is DashboardRoutes.DocPreview -> navigateToDocPreviewScreen(route.filePath)
            is DashboardRoutes.DocScanning -> navigateToDocScanningScreen(route.docFile)
        }
    }


    private fun navigateToDocPreviewScreen(docFilePath: String) {
        navController.navigate(DashboardFragmentDirections.actionDocPreviewFragment(docFilePath))
    }


    private fun navigateToDocScanningScreen(docFile: Uri) {
        navController.navigate(DashboardFragmentDirections.actionDocScanningFragment(docFile))
    }


}