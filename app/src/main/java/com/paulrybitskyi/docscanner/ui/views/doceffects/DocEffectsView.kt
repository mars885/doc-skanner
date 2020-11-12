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

package com.paulrybitskyi.docscanner.ui.views.doceffects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.paulrybitskyi.commons.ktx.layoutInflater
import com.paulrybitskyi.commons.ktx.showToast
import com.paulrybitskyi.commons.utils.observeChanges
import com.paulrybitskyi.docscanner.R
import com.paulrybitskyi.docscanner.databinding.ViewDocEffectsBinding
import com.paulrybitskyi.docscanner.utils.*
import com.paulrybitskyi.docscanner.utils.ImageEffectTransformationProvider
import com.paulrybitskyi.docscanner.utils.into
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Transformation
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
internal class DocEffectsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val hasImageUri: Boolean
        get() = (imageUri != null)

    private var isProgressBarVisible: Boolean
        set(value) { binding.progressBar.isVisible = value }
        get() = binding.progressBar.isVisible

    private val currentBitmap: Bitmap?
        get() = (binding.imageView.drawable as? BitmapDrawable)?.bitmap

    private var originalBitmapTarget: Target? = null

    private val binding = ViewDocEffectsBinding.inflate(context.layoutInflater, this)

    var imageUri by observeChanges<Uri?>(null) { _, _ ->
        reloadImage()
    }

    var effect by observeChanges<Effect?>(null) { oldEffect, newEffect ->
        if(newEffect != oldEffect) reloadImage()
    }

    @Inject lateinit var stringProvider: StringProvider
    @Inject lateinit var picasso: Picasso
    @Inject lateinit var imageEffectTransformationProvider: ImageEffectTransformationProvider

    var onApplyingEffectStarted: (() -> Unit)? = null
    var onApplyingEffectFinished: (() -> Unit)? = null


    internal enum class Effect {

        NONE,
        MAGIC_COLOR,
        GRAY_MODE,
        BLACK_AND_WHITE

    }


    init {
        initDefaults()
    }


    private fun initDefaults() {
        isProgressBarVisible = false
    }


    private fun reloadImage() {
        if(!hasImageUri) return

        isProgressBarVisible = true

        picasso.load(checkNotNull(imageUri))
            .apply {
                effect?.getEffectTransformation()?.let(::transform)
                into(binding.imageView, onSuccess = ::onImageLoaded)
            }
    }


    private fun Effect.getEffectTransformation(): Transformation? {
        return when(this) {
            Effect.NONE -> null
            Effect.MAGIC_COLOR -> imageEffectTransformationProvider.provideMagicColorTransformation()
            Effect.GRAY_MODE -> imageEffectTransformationProvider.provideGrayscaleTransformation()
            Effect.BLACK_AND_WHITE -> imageEffectTransformationProvider.provideBinaryTransformation()
        }
    }


    private fun onImageLoaded() {
        isProgressBarVisible = false
    }


    fun applyEffect(onSuccess: (Bitmap) -> Unit) {
        if(!hasImageUri) return

        originalBitmapTarget = TargetAdapter(
            onLoaded = {
                onApplyingEffectFinished?.invoke()
                onSuccess(it)
            },
            onFailed = ::onOriginalBitmapLoadingFailed
        )

        onApplyingEffectStarted?.invoke()

        picasso.load(checkNotNull(imageUri))
            .apply {
                effect?.getEffectTransformation()?.let(::transform)
                into(checkNotNull(originalBitmapTarget))
            }
    }


    private fun onOriginalBitmapLoadingFailed(error: Exception) {
        onApplyingEffectFinished?.invoke()

        context.showToast(stringProvider.getString(R.string.error_effect_application_failed))
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        currentBitmap?.recycle()
    }


}