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

package com.paulrybitskyi.docskanner.ui.views.docs

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import com.paulrybitskyi.commons.ktx.getColor
import com.paulrybitskyi.commons.ktx.getDimension
import com.paulrybitskyi.commons.ktx.layoutInflater
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.databinding.ViewDocBinding

internal class DocView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {


    private val binding = ViewDocBinding.inflate(context.layoutInflater, this)

    private var areDetailsVisible: Boolean
        set(value) { binding.detailsTv.isVisible = value }
        get() = binding.detailsTv.isVisible

    var name: CharSequence
        set(value) { binding.nameTv.text = value }
        get() = binding.nameTv.text

    var details: CharSequence?
        set(value) {
            areDetailsVisible = (value != null)
            binding.detailsTv.text = value
        }
        get() = binding.detailsTv.text


    init {
        initCard()
    }


    private fun initCard() {
        setCardBackgroundColor(getColor(R.color.doc_card_background_color))
        cardElevation = getDimension(R.dimen.doc_card_elevation)
        radius = getDimension(R.dimen.doc_card_corner_radius)
    }


}