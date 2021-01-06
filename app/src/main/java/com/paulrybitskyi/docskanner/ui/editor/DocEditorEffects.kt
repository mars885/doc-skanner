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

package com.paulrybitskyi.docskanner.ui.editor

import com.paulrybitskyi.docskanner.ui.base.events.Command
import com.paulrybitskyi.docskanner.ui.base.events.Route
import com.paulrybitskyi.docskanner.utils.dialogs.DialogConfig


internal sealed class DocEditorCommand : Command {

    object ApplyGrayEffect : DocEditorCommand()

    object ApplyFirstBawEffect : DocEditorCommand()

    object ApplySecondBawEffect : DocEditorCommand()

    object ClearEffect : DocEditorCommand()

    class ShowDialog(val config: DialogConfig): DocEditorCommand()

}


internal sealed class DocEditorRoute : Route {

    object Dashboard : DocEditorRoute()

    object NavigateBack : DocEditorRoute()

}