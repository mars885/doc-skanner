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

package com.paulrybitskyi.docskanner.ui.effects

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.paulrybitskyi.commons.ktx.onClick
import com.paulrybitskyi.commons.ktx.removeElevation
import com.paulrybitskyi.commons.navigation.navController
import com.paulrybitskyi.commons.utils.viewBinding
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.databinding.FragmentDocEffectsBinding
import com.paulrybitskyi.docskanner.ui.base.BaseFragment
import com.paulrybitskyi.docskanner.ui.base.events.Command
import com.paulrybitskyi.docskanner.ui.base.events.Route
import com.paulrybitskyi.docskanner.ui.views.DocEffectsView
import com.paulrybitskyi.docskanner.utils.dialogs.Dialog
import com.paulrybitskyi.docskanner.utils.dialogs.DialogBuilder
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import com.paulrybitskyi.docskanner.utils.dialogs.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class DocEffectsFragment : BaseFragment<
    FragmentDocEffectsBinding,
    DocEffectsViewModel
>(R.layout.fragment_doc_effects) {


    override val viewBinding by viewBinding(FragmentDocEffectsBinding::bind)
    override val viewModel by viewModels<DocEffectsViewModel>()

    @Inject lateinit var dialogBuilder: DialogBuilder

    private var dialog: Dialog? = null


    override fun onInit() {
        super.onInit()

        initToolbar()
        initEffects()
        initDocEffects()
        initButtons()
    }


    private fun initToolbar() = with(viewBinding.toolbar) {
        removeElevation()
        onLeftButtonClickListener = { viewModel.onToolbarLeftButtonClicked() }
    }


    private fun initEffects() = with(viewBinding) {
        magicColorEffectIv.isTitleTextOneLiner = true
        magicColorEffectIv.onClick { viewModel.onMagicColorEffectClicked() }

        grayModeEffectIv.isTitleTextOneLiner = true
        grayModeEffectIv.onClick { viewModel.onGrayModeEffectClicked() }

        blackAndWhiteEffectIv.isTitleTextOneLiner = true
        blackAndWhiteEffectIv.onClick { viewModel.onBlackAndWhiteEffectClicked() }
    }


    private fun initDocEffects() = with(viewBinding.docEffectsView) {
        onApplyingEffectStarted = viewModel::onApplyingEffectStarted
        onApplyingEffectFinished = viewModel::onApplyingEffectFinished
    }


    private fun initButtons() = with(viewBinding) {
        clearEffectBtnTv.onClick { viewModel.onClearButtonClicked() }
        saveBtnTv.onClick {
            viewBinding.docEffectsView.applyEffect(viewModel::onSaveButtonClicked)
        }
    }


    override fun onBindViewModel() = with(viewModel) {
        super.onBindViewModel()

        observeToolbarVisibility()
        observeSaveButtonEnabledState()
        observeDocFile()
    }


    private fun DocEffectsViewModel.observeToolbarVisibility() {
        toolbarProgressBarVisibility.observe(viewLifecycleOwner) {
            viewBinding.toolbarPb.isVisible = it
        }
    }


    private fun DocEffectsViewModel.observeSaveButtonEnabledState() {
        saveButtonEnabledState.observe(viewLifecycleOwner) {
            viewBinding.saveBtnTv.isEnabled = it
        }
    }


    private fun DocEffectsViewModel.observeDocFile() {
        docFile.observe(viewLifecycleOwner) {
            viewBinding.docEffectsView.imageUri = it
        }
    }


    override fun onHandleCommand(command: Command) {
        super.onHandleCommand(command)

        when(command) {
            is DocEffectsCommands.ApplyMagicColorEffect -> applyMagicColorEffect()
            is DocEffectsCommands.ApplyGrayModeEffect -> applyGrayModeEffect()
            is DocEffectsCommands.ApplyBlackAndWhiteEffect -> applyBlackAndWhiteEffect()
            is DocEffectsCommands.ClearEffect -> clearEffect()
            is DocEffectsCommands.ShowDialog -> showDialog(command.config)
        }
    }


    private fun applyMagicColorEffect() {
        viewBinding.docEffectsView.effect = DocEffectsView.Effect.MAGIC_COLOR
    }


    private fun applyGrayModeEffect() {
        viewBinding.docEffectsView.effect = DocEffectsView.Effect.GRAY_MODE
    }


    private fun applyBlackAndWhiteEffect() {
        viewBinding.docEffectsView.effect = DocEffectsView.Effect.BLACK_AND_WHITE
    }


    private fun clearEffect() {
        viewBinding.docEffectsView.effect = DocEffectsView.Effect.NONE
    }


    private fun showDialog(config: DialogConfig) {
        dialog?.dismiss()
        dialog = dialogBuilder.buildDialog(requireContext(), config).show(viewLifecycleOwner)
    }


    override fun onRoute(route: Route) {
        super.onRoute(route)

        when(route) {
            is DocEffectsRoutes.Dashboard -> navigateToDashboardScreen()
            is DocEffectsRoutes.NavigateBack -> navigateBack()
        }
    }


    private fun navigateToDashboardScreen() {
        navController.navigate(DocEffectsFragmentDirections.actionDashboardFragment())
    }


    private fun navigateBack() {
        navController.popBackStack()
    }


}