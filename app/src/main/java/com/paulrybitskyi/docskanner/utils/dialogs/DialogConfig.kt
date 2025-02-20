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

package com.paulrybitskyi.docskanner.utils.dialogs


internal class DialogConfig(
    val content: DialogContent,
    val title: String = "",
    val negativeBtnText: String = "",
    val positiveBtnText: String = "",
    val isCancelable: Boolean = true,
    val negativeBtnClick: (() -> Unit)? = null,
    val positiveBtnClick: (() -> Unit)? = null,
    val onShown: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
) {

    val hasTitle: Boolean
        get() = title.isNotBlank()

    val hasNegativeBtnText: Boolean
        get() = negativeBtnText.isNotBlank()

    val hasPositiveBtnText: Boolean
        get() = positiveBtnText.isNotBlank()

}


internal sealed class DialogContent {

    class Info(val message: String): DialogContent()

    class Input(
        val hint: String = "",
        val prefill: String = "",
        val callback: ((String) -> Unit)?
    ): DialogContent()

    class List(
        val items: kotlin.collections.List<DialogItem>,
        val selectedItemIndex: Int = -1,
        val mode: DialogListMode = DialogListMode.STANDARD,
        val callback: ((DialogItem) -> Unit)?
    ): DialogContent()

}


internal enum class DialogListMode {

    STANDARD,
    SINGLE_CHOICE

}


internal class DialogItem(
    val title: String,
    val tag: Any? = null
)