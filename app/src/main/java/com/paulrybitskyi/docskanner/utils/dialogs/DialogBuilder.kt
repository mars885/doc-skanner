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

package com.paulrybitskyi.docskanner.utils.dialogs

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.paulrybitskyi.commons.ktx.getCompatColor
import com.paulrybitskyi.commons.ktx.toColorStateList
import com.paulrybitskyi.docskanner.R
import javax.inject.Inject


internal interface DialogBuilder {

    fun buildDialog(context: Context, config: DialogConfig): Dialog

}


internal class DialogBuilderImpl @Inject constructor() : DialogBuilder {


    override fun buildDialog(context: Context, config: DialogConfig): Dialog {
        return MaterialDialog.Builder(context)
            .apply {
                addTitle(config)
                addContent(config)
                addInput(config)
                addListItems(config)
                addNegativeText(config)
                addPositiveText(config)
                setCancellableFlag(config)
                addListeners(config)
                applyStyling(context)
            }
            .build()
            .let(::DocSkannerDialog)
    }


    private fun MaterialDialog.Builder.addTitle(config: DialogConfig) {
        if(config.hasTitle) title(config.title)
    }


    private fun MaterialDialog.Builder.addContent(config: DialogConfig) {
        if(config.hasContent) content(config.content)
    }


    private fun MaterialDialog.Builder.addInput(config: DialogConfig) {
        if(config.hasInput) {
            input(config.inputHint, config.inputPrefill) { _, text ->
                config.inputCallback?.invoke(text.toString())
            }
        }
    }


    private fun MaterialDialog.Builder.addListItems(config: DialogConfig) {
        if(config.hasItems) {
            items(config.items.map(DialogItem::title))

            if(config.hasItemsCallback) {
                itemsCallback { _, _, index, _ ->
                    config.itemsCallback?.invoke(config.items[index])
                }
            }

            if(config.hasItemsCallbackSingleChoice && config.hasSelectedItemIndex) {
                itemsCallbackSingleChoice(config.selectedItemIndex) { _, _, index, _ ->
                    config.itemsCallbackSingleChoice?.invoke(config.items[index])
                    true
                }
            }
        }
    }


    private fun MaterialDialog.Builder.addNegativeText(config: DialogConfig) {
        if(config.hasNegativeBtnText) negativeText(config.negativeBtnText)
    }


    private fun MaterialDialog.Builder.addPositiveText(config: DialogConfig) {
        if(config.hasPositiveBtnText) positiveText(config.positiveBtnText)
    }


    private fun MaterialDialog.Builder.setCancellableFlag(config: DialogConfig) {
        cancelable(config.isCancelable)
    }


    private fun MaterialDialog.Builder.addListeners(config: DialogConfig) {
        onNegative { _, _ -> config.negativeBtnClick?.invoke() }
        onPositive { _, _ -> config.positiveBtnClick?.invoke() }

        showListener { config.onShown?.invoke() }
        dismissListener { config.onDismiss?.invoke() }
    }


    private fun MaterialDialog.Builder.applyStyling(context: Context) {
        backgroundColor(context.getCompatColor(R.color.dialog_background_color))
        titleColor(context.getCompatColor(R.color.dialog_title_color))
        contentColor(context.getCompatColor(R.color.dialog_text_color))
        itemsColor(context.getCompatColor(R.color.dialog_text_color))
        choiceWidgetColor(context.getCompatColor(R.color.dialog_widget_color).toColorStateList())
        positiveColor(context.getCompatColor(R.color.dialog_button_color))
        negativeColor(context.getCompatColor(R.color.dialog_button_color))
    }


}