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

package com.paulrybitskyi.docskanner.ui.views.docs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paulrybitskyi.commons.ktx.onClick
import com.paulrybitskyi.docskanner.ui.views.base.AbstractItem
import com.paulrybitskyi.docskanner.ui.views.base.HasListeners
import com.paulrybitskyi.docskanner.ui.views.base.NoDependencies

internal class DocItem(model: DocModel): AbstractItem<
    DocModel,
    DocItem.ViewHolder,
    NoDependencies
>(model) {


    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        dependencies: NoDependencies
    ): ViewHolder {
        return ViewHolder(DocView(parent.context))
    }


    override fun performBinding(viewHolder: ViewHolder, dependencies: NoDependencies) {
        viewHolder.bind(model)
    }


    internal class ViewHolder(
        private val view: DocView
    ): RecyclerView.ViewHolder(view), HasListeners {

        fun bind(model: DocModel) = with(view) {
            name = model.name
            details = model.details
        }

        fun setOnClickListener(listener: () -> Unit) {
            view.onClick { listener() }
        }

    }


}