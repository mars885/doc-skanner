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

package com.paulrybitskyi.docskanner.ui.dashboard.fragment

import android.net.Uri
import com.paulrybitskyi.docskanner.ui.base.events.Command
import com.paulrybitskyi.docskanner.ui.base.events.Route
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig
import java.io.File


internal sealed class DashboardCommand : Command {

    class ShowDialog(val config: DialogConfig): DashboardCommand()

    object RequestCameraPermission: DashboardCommand()

    class TakeCameraImage(val destinationUri: Uri): DashboardCommand()

    object PickGalleryImage: DashboardCommand()

}


internal sealed class DashboardRoute : Route {

    data class DocPreview(val docFile: File): DashboardRoute()

    data class DocScanner(val docImageFile: File): DashboardRoute()

}