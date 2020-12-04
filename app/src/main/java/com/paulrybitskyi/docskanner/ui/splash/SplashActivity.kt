package com.paulrybitskyi.docskanner.ui.splash

import android.Manifest
import androidx.activity.viewModels
import com.karumi.dexter.Dexter
import com.paulrybitskyi.docskanner.databinding.ActivitySplashBinding
import com.paulrybitskyi.docskanner.ui.base.BaseActivity
import com.paulrybitskyi.docskanner.ui.base.events.Command
import com.paulrybitskyi.docskanner.ui.base.events.Route
import com.paulrybitskyi.docskanner.ui.dashboard.DashboardActivity
import com.paulrybitskyi.docskanner.utils.dialogs.Dialog
import com.paulrybitskyi.docskanner.utils.dialogs.DialogBuilder
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import com.paulrybitskyi.docskanner.utils.dialogs.show
import com.paulrybitskyi.docskanner.core.utils.withListener
import com.paulrybitskyi.docskanner.utils.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {


    override val viewBinding by viewBinding(ActivitySplashBinding::inflate)
    override val viewModel by viewModels<SplashViewModel>()

    @Inject lateinit var dialogBuilder: DialogBuilder

    private var dialog: Dialog? = null


    override fun onLoadData() {
        super.onLoadData()

        viewModel.init()
    }


    override fun onHandleCommand(command: Command) {
        super.onHandleCommand(command)

        when(command) {
            is SplashCommands.RequestStoragePermission -> requestStoragePermission()
            is SplashCommands.ShowDialog -> showDialog(command.config)
        }
    }


    private fun requestStoragePermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(
                onPermissionGranted = { viewModel.onStoragePermissionGranted() },
                onPermissionDenied = { viewModel.onStoragePermissionDenied() }
            )
            .check()
    }


    private fun showDialog(config: DialogConfig) {
        dialog?.dismiss()
        dialog = dialogBuilder.buildDialog(this, config).show(lifecycle)
    }


    override fun onRoute(route: Route) {
        super.onRoute(route)

        when(route) {
            is SplashRoutes.Dashboard -> navigateToDashboard()
            is SplashRoutes.Exit -> exitTheApp()
        }
    }


    private fun navigateToDashboard() {
        startActivity(DashboardActivity.newIntent(this))
        finish()
    }


    private fun exitTheApp() {
        finish()
    }


}