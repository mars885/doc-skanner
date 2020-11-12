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

package com.paulrybitskyi.docscanner.ui.views.docs

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paulrybitskyi.commons.ktx.*
import com.paulrybitskyi.commons.recyclerview.decorators.spacing.SpacingItemDecorator
import com.paulrybitskyi.commons.recyclerview.decorators.spacing.policies.LastItemExclusionPolicy
import com.paulrybitskyi.commons.recyclerview.utils.disableChangeAnimations
import com.paulrybitskyi.commons.utils.observeChanges
import com.paulrybitskyi.docscanner.R
import com.paulrybitskyi.docscanner.databinding.ViewDocsBinding
import com.paulrybitskyi.docscanner.utils.fadeIn
import com.paulrybitskyi.docscanner.utils.resetAnimation

internal class DocsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val binding = ViewDocsBinding.inflate(context.layoutInflater, this)

    private lateinit var adapter: DocsAdapter

    private var adapterItems by observeChanges<List<DocItem>>(emptyList()) { _, newItems ->
        adapter.submitList(newItems)
    }

    var uiState by observeChanges<DocsUiState>(DocsUiState.Empty) { _, newState ->
        handleUiStateChange(newState)
    }

    var onDocClickListener: ((DocModel) -> Unit)? = null


    init {
        initRecyclerView()
        initInfoView()
        initDefaults()
    }


    private fun initRecyclerView() = with(binding.recyclerView) {
        disableChangeAnimations()
        layoutManager = initLayoutManager(context)
        adapter = initAdapter()
        addItemDecoration(initItemDecorator())
    }


    private fun initLayoutManager(context: Context): LinearLayoutManager {
        return object : LinearLayoutManager(context) {

            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                return RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }

        }
    }


    private fun initAdapter(): DocsAdapter {
        return DocsAdapter(context)
            .apply { listenerBinder = ::bindListener }
            .also { adapter = it }
    }


    private fun bindListener(item: DocItem, viewHolder: RecyclerView.ViewHolder) {
        if(viewHolder is DocItem.ViewHolder) {
            viewHolder.setOnClickListener { onDocClickListener?.invoke(item.model) }
        }
    }


    private fun initItemDecorator(): SpacingItemDecorator {
        return SpacingItemDecorator(
            spacing = getDimensionPixelSize(R.dimen.docs_recycler_view_decorator_spacing),
            sideFlags = SpacingItemDecorator.SIDE_BOTTOM,
            itemExclusionPolicy = LastItemExclusionPolicy()
        )
    }


    private fun initInfoView() = with(binding.infoView) {
        isDescriptionTextVisible = false
    }


    private fun initDefaults() {
        uiState = uiState
    }


    private fun handleUiStateChange(newState: DocsUiState) {
        when(newState) {
            is DocsUiState.Empty -> onEmptyStateSelected()
            is DocsUiState.Loading -> onLoadingStateSelected()
            is DocsUiState.Result -> onResultStateSelected(newState)
        }
    }


    private fun onEmptyStateSelected() {
        showInfoView()
        hideProgressBar()
        hideRecyclerView()
    }


    private fun onLoadingStateSelected() {
        showProgressBar()
        hideInfoView()
        hideRecyclerView()
    }


    private fun onResultStateSelected(uiState: DocsUiState.Result) {
        adapterItems = uiState.model.toAdapterItems()

        showRecyclerView()
        hideInfoView()
        hideProgressBar()
    }


    private fun showInfoView() = with(binding.infoView) {
        if(isVisible) return

        makeVisible()
        fadeIn()
    }


    private fun hideInfoView() = with(binding.infoView) {
        makeGone()
        resetAnimation()
    }


    private fun showProgressBar() = with(binding.progressBar) {
        makeVisible()
    }


    private fun hideProgressBar() = with(binding.progressBar) {
        makeGone()
    }


    private fun showRecyclerView() = with(binding.recyclerView) {
        if(isVisible) return

        makeVisible()
        fadeIn()
    }


    private fun hideRecyclerView() = with(binding.recyclerView) {
        makeInvisible()
        resetAnimation()
    }


    private fun List<DocModel>.toAdapterItems(): List<DocItem> {
        return map(::DocItem)
    }


}