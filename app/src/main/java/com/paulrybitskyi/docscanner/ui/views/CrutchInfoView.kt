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

package com.paulrybitskyi.docscanner.ui.views

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.paulrybitskyi.commons.ktx.*
import com.paulrybitskyi.commons.ktx.views.setTextSizeInPx
import com.paulrybitskyi.docscanner.R
import com.paulrybitskyi.docscanner.databinding.ViewInfoBinding

class CrutchInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private val binding = ViewInfoBinding.inflate(context.layoutInflater, this)

    var isTitleOneLiner: Boolean = false
        set(value) {
            field = value

            binding.titleTv.maxLines = 1
            binding.titleTv.ellipsize = TextUtils.TruncateAt.END
        }

    var isDescriptionTextVisible: Boolean
        set(value) { binding.descriptionTv.isVisible = value }
        get() = binding.descriptionTv.isVisible

    var iconSize: Int = getDimensionPixelSize(R.dimen.info_view_icon_size)
        set(value) {
            field = value
            binding.iconIv.setLayoutParamsSize(value)
        }

    var titleTextTopMargin: Int
        set(value) { binding.titleTv.topMargin = value }
        get() = binding.titleTv.topMargin

    var descriptionTextTopMargin: Int
        set(value) { binding.descriptionTv.topMargin = value }
        get() = binding.descriptionTv.topMargin

    @get:ColorInt
    var iconColor: Int = getColor(R.color.info_view_icon_color)
        set(@ColorInt value) {
            field = value
            icon = icon
        }

    @get:ColorInt
    var titleTextColor: Int
        set(@ColorInt value) { binding.titleTv.setTextColor(value) }
        get() = binding.titleTv.currentTextColor

    @get:ColorInt
    var descriptionTextColor: Int
        set(@ColorInt value) { binding.descriptionTv.setTextColor(value) }
        get() = binding.descriptionTv.currentTextColor

    var titleTextSize: Float
        set(value) { binding.titleTv.setTextSizeInPx(value) }
        get() = binding.titleTv.textSize

    var descriptionTextSize: Float
        set(value) { binding.descriptionTv.setTextSizeInPx(value) }
        get() = binding.descriptionTv.textSize

    var titleTextTypeface: Typeface
        set(value) { binding.titleTv.typeface = value }
        get() = binding.titleTv.typeface

    var descriptionTextTypeface: Typeface
        set(value) { binding.descriptionTv.typeface = value }
        get() = binding.descriptionTv.typeface

    var titleText: CharSequence
        set(value) { binding.titleTv.text = value}
        get() = binding.titleTv.text

    var descriptionText: CharSequence
        set(value) {
            isDescriptionTextVisible = value.isNotBlank()
            binding.descriptionTv.text = value
        }
        get() = binding.descriptionTv.text

    var icon: Drawable?
        set(value) { binding.iconIv.setImageDrawable(value?.setColor(iconColor)) }
        get() = binding.iconIv.drawable


    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        attrs?.let { extractAttributes(it, defStyleAttr) }
    }


    private fun extractAttributes(attrs: AttributeSet, defStyleAttr: Int) {
        context.withStyledAttributes(
            set = attrs,
            attrs = R.styleable.InfoView,
            defStyleAttr = defStyleAttr
        ) {
            iconSize = getDimensionPixelSize(R.styleable.InfoView_infoView_iconSize, iconSize)
            titleTextSize = getDimension(R.styleable.InfoView_infoView_titleTextSize, titleTextSize)
            descriptionTextSize = getDimension(R.styleable.InfoView_infoView_descriptionTextSize, descriptionTextSize)
            titleTextTopMargin = getDimensionPixelSize(R.styleable.InfoView_infoView_titleTextTopMargin, titleTextTopMargin)
            descriptionTextTopMargin = getDimensionPixelSize(R.styleable.InfoView_infoView_descriptionTextTopMargin, descriptionTextTopMargin)
            iconColor = getColor(R.styleable.InfoView_infoView_iconColor, iconColor)
            titleTextColor = getColor(R.styleable.InfoView_infoView_titleTextColor, titleTextColor)
            descriptionTextColor = getColor(R.styleable.InfoView_infoView_descriptionTextColor, descriptionTextColor)
            titleTextTypeface = getFont(context, R.styleable.InfoView_infoView_titleTextFont, titleTextTypeface)
            descriptionTextTypeface = getFont(context, R.styleable.InfoView_infoView_descriptionTextFont, descriptionTextTypeface)
            icon = getDrawable(R.styleable.InfoView_infoView_icon)
            titleText = getString(R.styleable.InfoView_infoView_titleText, titleText)
            descriptionText = getString(R.styleable.InfoView_infoView_descriptionText, descriptionText)
        }
    }


}