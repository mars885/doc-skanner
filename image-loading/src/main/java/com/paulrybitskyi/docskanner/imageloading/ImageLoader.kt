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

import com.paulrybitskyi.docskanner.imageloading.Config.Destination
import com.paulrybitskyi.docskanner.imageloading.utils.PicassoTarget
import com.paulrybitskyi.docskanner.imageloading.utils.PicassoTransformation
import com.paulrybitskyi.docskanner.imageloading.utils.into
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton


interface ImageLoader {

    fun loadImage(config: Config)

    fun cancelRequests(destination: Destination)

}


@Singleton
internal class ImageLoaderImpl @Inject constructor(
    private val picasso: Picasso
) : ImageLoader {


    private val targetsMap = ConcurrentHashMap<String, PicassoTarget>()


    override fun loadImage(config: Config) {
        config.onStart?.invoke()

        val requestCreator = when(config.source) {
            is Config.Source.Url -> picasso.load(config.source.url)
            is Config.Source.Uri -> picasso.load(config.source.uri)
            is Config.Source.File -> picasso.load(config.source.file)
        }

        if(config.shouldCenterCrop) requestCreator.centerCrop()
        if(config.shouldCenterInside) requestCreator.centerInside()
        if(config.shouldFit) requestCreator.fit()
        if(config.hasTargetSize) requestCreator.resize(config.targetWidth, config.targetHeight)
        if(config.hasRotationDegrees) requestCreator.rotate(config.rotationDegrees)

        config.progressDrawable?.let(requestCreator::placeholder)
        config.errorDrawable?.let(requestCreator::error)

        if(config.hasTransformations) {
            config.transformations
                .map(::PicassoTransformation)
                .let { requestCreator.transform(it) }
        }

        when(config.destination) {
            is Destination.View -> requestCreator.loadIntoImageView(config, config.destination)
            is Destination.Callback -> requestCreator.loadIntoCallback(config, config.destination)
        }
    }


    private fun RequestCreator.loadIntoImageView(config: Config, destination: Destination.View) {
        if(config.hasAtLeastOneResultListener) {
            into(
                target = destination.imageView,
                onSuccess = config.onSuccess,
                onFailure = config.onFailure
            )
        } else {
            into(destination.imageView)
        }
    }


    private fun RequestCreator.loadIntoCallback(config: Config, destination: Destination.Callback) {
        val configKey = config.toKey()
        val picassoTarget = createPicassoTargetWithStrongReference(configKey, destination)

        into(picassoTarget)
    }


    private fun createPicassoTargetWithStrongReference(
        configKey: String,
        destination: Destination.Callback
    ): PicassoTarget {
        val targetWrapper = wrapTarget(destination.target, configKey)
        val picassoTarget = PicassoTarget(targetWrapper)

        return picassoTarget.also {
            targetsMap[configKey] = picassoTarget
        }
    }


    private fun wrapTarget(target: Target, configKey: String): TargetAdapter {
        return TargetAdapter(
            onLoaded = { bitmap ->
                target.onBitmapLoadingSucceeded(bitmap)
                targetsMap.remove(configKey)
            },
            onFailed = { error ->
                target.onBitmapLoadingFailed(error)
                targetsMap.remove(configKey)
            },
            onPrepareToLoad = target::onPrepareLoad
        )
    }


    override fun cancelRequests(destination: Destination) {
        when(destination) {
            is Destination.View -> picasso.cancelRequest(destination.imageView)
            is Destination.Callback -> targetsMap.clear()
        }
    }


}