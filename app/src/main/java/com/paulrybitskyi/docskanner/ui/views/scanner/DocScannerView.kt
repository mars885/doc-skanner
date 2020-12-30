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

package com.paulrybitskyi.docskanner.ui.views.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.paulrybitskyi.commons.ktx.getDimensionPixelSize
import com.paulrybitskyi.commons.ktx.layoutInflater
import com.paulrybitskyi.commons.ktx.showToast
import com.paulrybitskyi.commons.utils.observeChanges
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.core.providers.CoroutineScopeProvider
import com.paulrybitskyi.docskanner.core.providers.DispatcherProvider
import com.paulrybitskyi.docskanner.core.providers.StringProvider
import com.paulrybitskyi.docskanner.databinding.ViewDocScannerBinding
import com.paulrybitskyi.docskanner.imageloading.Config
import com.paulrybitskyi.docskanner.imageloading.ImageLoader
import com.paulrybitskyi.docskanner.imageloading.Target
import com.paulrybitskyi.docskanner.imageloading.TargetAdapter
import com.paulrybitskyi.docskanner.imageprocessing.crop.transform.CropTransformationFactory
import com.paulrybitskyi.docskanner.imageprocessing.crop.transform.Size
import com.paulrybitskyi.docskanner.imageprocessing.detector.DocShapeDetector
import com.paulrybitskyi.docskanner.ui.views.scanner.crop.toCropCoords
import com.paulrybitskyi.docskanner.ui.views.scanner.crop.toDocCropArea
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
internal class DocScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val imageMargin = context.getDimensionPixelSize(R.dimen.doc_scanner_image_margin)
    private val docCropAreaPointerSize = context.getDimensionPixelSize(R.dimen.doc_crop_area_pointer_size)

    private val hasImageFile: Boolean
        get() = (imageFile != null)

    private var isImageVisible: Boolean
        set(value) { binding.imageView.isVisible = value }
        get() = binding.imageView.isVisible

    private var isDocCropAreaVisible: Boolean
        set(value) { binding.docCropAreaView.isVisible = value }
        get() = binding.docCropAreaView.isVisible

    private var isProgressBarVisible: Boolean
        set(value) { binding.progressBar.isVisible = value }
        get() = binding.progressBar.isVisible

    private var shouldRunShapeDetection = true

    private var currentImageRotation = 0f
        set(value) {
            field = (value % 360)
            shouldRunShapeDetection = true
            reloadImage()
        }

    private val currentBitmap: Bitmap?
        get() = (binding.imageView.drawable as? BitmapDrawable)?.bitmap

    private var scannedDocTarget: Target? = null

    private val binding = ViewDocScannerBinding.inflate(context.layoutInflater, this)

    var imageFile by observeChanges<File?>(null) { _, _ ->
        reloadImage()
    }

    @Inject lateinit var stringProvider: StringProvider
    @Inject lateinit var imageLoader: ImageLoader
    @Inject lateinit var coroutineScopeProvider: CoroutineScopeProvider
    @Inject lateinit var dispatcherProvider: DispatcherProvider
    @Inject lateinit var docShapeDetector: DocShapeDetector
    @Inject lateinit var cropTransformationFactory: CropTransformationFactory


    init {
        initDefaults()
    }


    private fun initDefaults() {
        isDocCropAreaVisible = false
        isProgressBarVisible = false
    }


    fun rotateLeft() {
        currentImageRotation -= 90f
    }


    fun rotateRight() {
        currentImageRotation += 90f
    }


    private fun reloadImage() {
        if(!hasImageFile) return

        isImageVisible = true
        isDocCropAreaVisible = false
        isProgressBarVisible = true

        val imageWidth = (width - (2 * imageMargin))
        val imageHeight = (height - (2 * imageMargin))

        imageLoader.loadImage(
            Config.Builder()
                .centerInside()
                .rotate(currentImageRotation)
                .resize(imageWidth, imageHeight)
                .source(Config.Source.File(checkNotNull(imageFile)))
                .destination(Config.Destination.View(binding.imageView))
                .onSuccess(::onImageLoaded)
                .build()
        )
    }


    private fun onImageLoaded() {
        resetDocCropArea()
        isProgressBarVisible = false
    }


    private fun resetDocCropArea() {
        val currentBitmap = checkNotNull(currentBitmap)

        binding.docCropAreaView.updateLayoutParams {
            this.width = (currentBitmap.width + docCropAreaPointerSize)
            this.height = (currentBitmap.height + docCropAreaPointerSize)
        }

        detectDocumentShape(currentBitmap)
    }


    private fun detectDocumentShape(currentBitmap: Bitmap) {
        if(!shouldRunShapeDetection) {
            isDocCropAreaVisible = true
            return
        }

        coroutineScopeProvider.launch(dispatcherProvider.computation) {
            val docShape = docShapeDetector.detectShape(currentBitmap)

            withContext(dispatcherProvider.main) {
                binding.docCropAreaView.setCropArea(docShape.toDocCropArea())

                shouldRunShapeDetection = false
                isDocCropAreaVisible = true
            }
        }
    }


    fun scanDocument(onSuccess: (Bitmap) -> Unit) {
        if(!hasImageFile) return

        if(!binding.docCropAreaView.hasValidArea()) {
            context.showToast(stringProvider.getString(R.string.error_cannot_crop))
            return
        }

        val docCropArea = checkNotNull(binding.docCropAreaView.getCropArea())
        val viewSize = Size(binding.imageView.width.toFloat(), binding.imageView.height.toFloat())
        val cropTransformation = cropTransformationFactory.createCropTransformation(
            cropCoords = docCropArea.toCropCoords(),
            viewSize = viewSize
        )

        scannedDocTarget = TargetAdapter(
            onLoaded = { onSuccess(it) },
            onFailed = ::onDocScanFailed
        )

        isImageVisible = false
        isDocCropAreaVisible = false
        isProgressBarVisible = true

        imageLoader.loadImage(
            Config.Builder()
                .transformation(cropTransformation)
                .rotate(currentImageRotation)
                .source(Config.Source.File(checkNotNull(imageFile)))
                .destination(Config.Destination.Callback(checkNotNull(scannedDocTarget)))
                .build()
        )
    }


    private fun onDocScanFailed(error: Exception) {
        context.showToast(stringProvider.getString(R.string.error_scan_failed))
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        coroutineScopeProvider.cancelChildren()
    }


}