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

package com.paulrybitskyi.docskanner.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.paulrybitskyi.commons.ktx.getDimensionPixelSize
import com.paulrybitskyi.commons.ktx.layoutInflater
import com.paulrybitskyi.commons.ktx.showToast
import com.paulrybitskyi.commons.utils.observeChanges
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.core.providers.StringProvider
import com.paulrybitskyi.docskanner.databinding.ViewDocCropperBinding
import com.paulrybitskyi.docskanner.imageloading.Config
import com.paulrybitskyi.docskanner.imageloading.ImageLoader
import com.paulrybitskyi.docskanner.imageloading.Target
import com.paulrybitskyi.docskanner.imageloading.TargetAdapter
import com.paulrybitskyi.docskanner.imageprocessing.cropping.CroppingTransformationFactory
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
internal class DocCropperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val imageMargin = context.getDimensionPixelSize(R.dimen.doc_cropper_image_margin)
    private val imageCropperPointerSize = context.getDimensionPixelSize(R.dimen.image_cropper_pointer_size)

    private val hasImageUri: Boolean
        get() = (imageUri != null)

    private var isImageVisible: Boolean
        set(value) { binding.imageView.isVisible = value }
        get() = binding.imageView.isVisible

    private var isImageCropperVisible: Boolean
        set(value) { binding.imageCropperView.isVisible = value }
        get() = binding.imageCropperView.isVisible

    private var isProgressBarVisible: Boolean
        set(value) { binding.progressBar.isVisible = value }
        get() = binding.progressBar.isVisible

    private var currentImageRotation = 0f
        set(value) {
            field = (value % 360)
            reloadImage()
        }

    private val currentBitmap: Bitmap?
        get() = (binding.imageView.drawable as? BitmapDrawable)?.bitmap

    private var originalBitmapTarget: Target? = null

    private val binding = ViewDocCropperBinding.inflate(context.layoutInflater, this)

    var imageUri by observeChanges<Uri?>(null) { _, _ ->
        reloadImage()
    }

    @Inject lateinit var stringProvider: StringProvider
    @Inject lateinit var imageLoader: ImageLoader
    @Inject lateinit var croppingTransformationFactory: CroppingTransformationFactory


    init {
        initDefaults()
    }


    private fun initDefaults() {
        isImageCropperVisible = false
        isProgressBarVisible = false

    }


    fun rotateLeft() {
        currentImageRotation -= 90f
    }


    fun rotateRight() {
        currentImageRotation += 90f
    }


    private fun reloadImage() {
        if(!hasImageUri) return

        isImageVisible = true
        isImageCropperVisible = false
        isProgressBarVisible = true

        val imageWidth = (width - (2 * imageMargin))
        val imageHeight = (height - (2 * imageMargin))

        imageLoader.loadImage(
            Config.Builder()
                .centerInside()
                .rotate(currentImageRotation)
                .resize(imageWidth, imageHeight)
                .source(Config.Source.Uri(checkNotNull(imageUri)))
                .destination(Config.Destination.View(binding.imageView))
                .onSuccess(::onImageLoaded)
                .build()
        )
    }


    private fun onImageLoaded() {
        resetImageCropper()
        isProgressBarVisible = false
    }


    private fun resetImageCropper() = with(binding.imageCropperView) {
        val currentBitmap = checkNotNull(currentBitmap)

        updateLayoutParams {
            this.width = (currentBitmap.width + imageCropperPointerSize)
            this.height = (currentBitmap.height + imageCropperPointerSize)
        }

        setImage(currentBitmap)
        isImageCropperVisible = true
    }


    fun cropBitmap(onSuccess: (Bitmap) -> Unit) {
        if(!hasImageUri) return

        if(!binding.imageCropperView.hasValidCroppingCoords()) {
            context.showToast(stringProvider.getString(R.string.error_cannot_crop))
            return
        }

        val viewSize = (binding.imageView.width.toFloat() to binding.imageView.height.toFloat())
        val croppingTransformation = croppingTransformationFactory.createCroppingTransformation(
            croppingCoords = binding.imageCropperView.getCroppingCoords(),
            viewSize = viewSize
        )

        originalBitmapTarget = TargetAdapter(
            onLoaded = { onSuccess(it) },
            onFailed = ::onOriginalBitmapLoadingFailed
        )

        isImageVisible = false
        isImageCropperVisible = false
        isProgressBarVisible = true

        imageLoader.loadImage(
            Config.Builder()
                .transformation(croppingTransformation)
                .rotate(currentImageRotation)
                .source(Config.Source.Uri(checkNotNull(imageUri)))
                .destination(Config.Destination.Callback(checkNotNull(originalBitmapTarget)))
                .build()
        )
    }


    private fun onOriginalBitmapLoadingFailed(error: Exception) {
        context.showToast(stringProvider.getString(R.string.error_cropping_failed))
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        currentBitmap?.recycle()
    }


}