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

package com.paulrybitskyi.docscanner.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target


internal fun RequestCreator.into(
    target: ImageView,
    onSuccess: (() -> Unit)? = null,
    onFailure: ((Exception) -> Unit)? = null
) {
    val callback = object : Callback {

        override fun onSuccess() {
            onSuccess?.invoke()
        }

        override fun onError(error: java.lang.Exception) {
            onFailure?.invoke(error)
        }

    }

    into(target, callback)
}


internal class TargetAdapter(
    private val onLoaded: (Bitmap) -> Unit,
    private val onFailed: (java.lang.Exception) -> Unit = {}
) : Target {


    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        onLoaded(bitmap)
    }


    override fun onBitmapFailed(exception: Exception, errorDrawable: Drawable?) {
        onFailed(exception)
    }


    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        // Ignore
    }


}