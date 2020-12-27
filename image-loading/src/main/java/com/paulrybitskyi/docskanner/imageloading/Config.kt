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

package com.paulrybitskyi.docskanner.imageloading

import android.graphics.drawable.Drawable
import android.widget.ImageView


class Config private constructor(
    val shouldCenterInside: Boolean,
    val shouldCenterCrop: Boolean,
    val shouldFit: Boolean,
    val targetWidth: Int,
    val targetHeight: Int,
    val rotationDegrees: Float,
    val progressDrawable: Drawable?,
    val errorDrawable: Drawable?,
    val source: Source,
    val destination: Destination,
    val transformations: List<Transformation>,
    val onStart: (() -> Unit)?,
    val onSuccess: (() -> Unit)?,
    val onFailure: ((Exception) -> Unit)?
) {


    sealed class Source {

        data class Url(val url: String): Source()

        data class Uri(val uri: android.net.Uri): Source()

        data class File(val file: java.io.File): Source()

    }


    sealed class Destination {

        class View(val imageView: ImageView): Destination()

        class Callback(val target: Target): Destination()

    }


    class Builder {

        private var shouldCenterInside: Boolean = false
        private var shouldCenterCrop: Boolean = false
        private var shouldFit: Boolean = false
        private var targetWidth: Int = 0
        private var targetHeight: Int = 0
        private var rotationDegrees: Float = 0f
        private var progressDrawable: Drawable? = null
        private var errorDrawable: Drawable? = null
        private var source: Source? = null
        private var destination: Destination? = null
        private var transformations: MutableList<Transformation> = mutableListOf()
        private var onStart: (() -> Unit)? = null
        private var onSuccess: (() -> Unit)? = null
        private var onFailure: ((Exception) -> Unit)? = null

        fun centerCrop() = apply { shouldCenterCrop = true }

        fun centerInside() = apply { shouldCenterInside = true }

        fun fit() = apply { shouldFit = true }

        fun resize(targetWidth: Int, targetHeight: Int) = apply {
            require(targetWidth > 0) { "The width must be larger tha 0." }
            require(targetHeight > 0) { "The height must be larger than 0." }

            this.targetWidth = targetWidth
            this.targetHeight = targetHeight
        }

        fun rotate(degrees: Float) = apply { this.rotationDegrees = degrees; }

        fun progressDrawable(drawable: Drawable) = apply { progressDrawable = drawable }

        fun errorDrawable(drawable: Drawable) = apply { errorDrawable = drawable }

        fun source(source: Source) = apply {
            if(source is Source.Url) {
                require(source.url.isNotBlank()) { "The image url is blank." }
            }

            this.source = source
        }

        fun destination(destination: Destination) = apply {
            this.destination = destination
        }

        fun transformation(transformation: Transformation) = apply {
            transformations.add(transformation)
        }

        fun transformations(transformations: List<Transformation>) = apply {
            this.transformations.addAll(transformations)
        }

        fun onStart(action: () -> Unit) = apply { onStart = action }

        fun onSuccess(action: () -> Unit) = apply { onSuccess = action }

        fun onFailure(action: (Exception) -> Unit) = apply { onFailure = action }

        fun build(): Config {
            check(source != null) { "The image source is not set." }
            check(destination != null) { "The image destination is not set." }

            return Config(
                shouldCenterInside = shouldCenterInside,
                shouldCenterCrop = shouldCenterCrop,
                shouldFit = shouldFit,
                targetWidth = targetWidth,
                targetHeight = targetHeight,
                rotationDegrees = rotationDegrees,
                progressDrawable = progressDrawable,
                errorDrawable = errorDrawable,
                source = checkNotNull(source),
                destination = checkNotNull(destination),
                transformations = transformations,
                onStart = onStart,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }

    }


}


internal val Config.hasTargetSize: Boolean
    get() = ((targetWidth > 0) && (targetHeight > 0))

internal val Config.hasRotationDegrees: Boolean
    get() = (rotationDegrees != 0f)

internal val Config.hasTransformations: Boolean
    get() = transformations.isNotEmpty()

internal val Config.hasAtLeastOneResultListener: Boolean
    get() = ((onSuccess != null) || (onFailure != null))


internal fun Config.toKey(): String {
    return buildString {
        if(shouldCenterInside) append("shouldCenterInside: $shouldCenterInside, ")
        if(shouldCenterCrop) append("shouldCenterCrop: $shouldCenterCrop, ")
        if(shouldFit) append("shouldFit: $shouldFit, ")
        if(hasTargetSize) append("targetSize: ($targetWidth, $targetHeight), ")
        if(hasRotationDegrees) append("rotationDegrees: $rotationDegrees, ")
        if(hasTransformations) {
            val transformationsKey = transformations.joinToString(transform = Transformation::key)

            append("transformations: $transformationsKey, ")
        }

        append("source: $source, destination: ${destination::class.simpleName}")
    }
}