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

package com.paulrybitskyi.docscanner.utils.dialogs


internal class DialogConfig(
    val title: String = "",
    val content: String = "",
    val items: List<DialogItem> = emptyList(),
    val selectedItemIndex: Int = -1,
    val negativeBtnText: String = "",
    val positiveBtnText: String = "",
    val isCancelable: Boolean = true,
    val itemsCallback: ((DialogItem) -> Unit)? = null,
    val itemsCallbackSingleChoice: ((DialogItem) -> Unit)? = null,
    val negativeBtnClick: (() -> Unit)? = null,
    val positiveBtnClick: (() -> Unit)? = null,
    val onShown: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
) {

    val hasTitle: Boolean
        get() = title.isNotBlank()

    val hasContent: Boolean
        get() = content.isNotBlank()

    val hasItems: Boolean
        get() = items.isNotEmpty()

    val hasSelectedItemIndex: Boolean
        get() = (selectedItemIndex != -1)

    val hasNegativeBtnText: Boolean
        get() = negativeBtnText.isNotBlank()

    val hasPositiveBtnText: Boolean
        get() = positiveBtnText.isNotBlank()

    val hasItemsCallback: Boolean
        get() = (itemsCallback != null)

    val hasItemsCallbackSingleChoice: Boolean
        get() = (itemsCallbackSingleChoice != null)

}


internal class DialogItem(
    val title: String,
    val tag: Any? = null
)