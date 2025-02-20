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

package com.paulrybitskyi.docskanner.ui.scanner

import com.paulrybitskyi.docskanner.ui.base.events.Command
import com.paulrybitskyi.docskanner.ui.base.events.Route
import java.io.File


internal sealed class DocScannerCommand : Command {

    object RotateImageLeft : DocScannerCommand()

    object RotateImageRight : DocScannerCommand()

}


internal sealed class DocScannerRoute : Route {

    data class DocEditor(val docImageFile: File) : DocScannerRoute()

    object NavigateBack : DocScannerRoute()

}