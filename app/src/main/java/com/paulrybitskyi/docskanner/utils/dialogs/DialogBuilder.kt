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

@file:Suppress("CheckResult")

package com.paulrybitskyi.docskanner.utils.dialogs

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.getInputLayout
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.paulrybitskyi.commons.ktx.bottomPadding
import com.paulrybitskyi.commons.ktx.clearPadding
import com.paulrybitskyi.commons.ktx.getCompatColor
import com.paulrybitskyi.commons.ktx.getDimensionPixelSize
import com.paulrybitskyi.docskanner.R
import javax.inject.Inject


internal interface DialogBuilder {

    fun buildDialog(context: Context, config: DialogConfig): Dialog

}


internal class DialogBuilderImpl @Inject constructor() : DialogBuilder {


    override fun buildDialog(context: Context, config: DialogConfig): Dialog {
        return MaterialDialog(context)
            .apply {
                addTitle(config)
                addContent(config)
                addButtons(config)
                addFlags(config)
                addListeners(config)
            }
            .let(::DocSkannerDialog)
    }


    private fun MaterialDialog.addTitle(config: DialogConfig) {
        if(config.hasTitle) title(text = config.title)
    }


    private fun MaterialDialog.addContent(config: DialogConfig) {
        when(config.content) {
            is DialogContent.Info -> constructInfoDialog(config.content)
            is DialogContent.Input -> constructInputDialog(config.content)
            is DialogContent.List -> constructListDialog(config.content)
        }
    }


    private fun MaterialDialog.constructInfoDialog(info: DialogContent.Info) {
        message(text = info.message)
    }


    private fun MaterialDialog.constructInputDialog(info: DialogContent.Input) {
        input(hint = info.hint, prefill = info.prefill) { _, text ->
            info.callback?.invoke(text.toString())
        }

        getInputLayout().boxBackgroundColor = context.getCompatColor(R.color.dialog_background_color)
        getInputLayout().boxStrokeColor = context.getCompatColor(R.color.dialog_input_underline_color)

        getInputField().setTextColor(context.getCompatColor(R.color.dialog_input_text_color))
        getInputField().setHintTextColor(context.getCompatColor(R.color.dialog_input_text_color))
        getInputField().clearPadding()
        getInputField().bottomPadding = context.getDimensionPixelSize(R.dimen.dialog_input_bottom_padding)
    }


    private fun MaterialDialog.constructListDialog(info: DialogContent.List) {
        val dialogItems = info.items.map(DialogItem::title)
        val callback = { index: Int -> info.callback?.invoke(info.items[index]) }

        if(info.mode == DialogListMode.STANDARD) {
            listItems(items = dialogItems) { _, index, _ -> callback(index) }
        } else if(info.mode == DialogListMode.SINGLE_CHOICE) {
            listItemsSingleChoice(
                items = dialogItems,
                initialSelection = info.selectedItemIndex
            ) { _, index, _ ->
                callback(index)
            }
        }
    }


    private fun MaterialDialog.addButtons(config: DialogConfig) {
        if(config.hasNegativeBtnText) {
            negativeButton(text = config.negativeBtnText) {
                config.negativeBtnClick?.invoke()
            }
        }

        if(config.hasPositiveBtnText) {
            positiveButton(text = config.positiveBtnText) {
                config.positiveBtnClick?.invoke()
            }
        }
    }


    private fun MaterialDialog.addFlags(config: DialogConfig) {
        cancelable(config.isCancelable)
    }


    private fun MaterialDialog.addListeners(config: DialogConfig) {
        onShow { config.onShown?.invoke() }
        onDismiss { config.onDismiss?.invoke() }
    }


}