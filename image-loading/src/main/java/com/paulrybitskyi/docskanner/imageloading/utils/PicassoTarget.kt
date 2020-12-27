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

package com.paulrybitskyi.docskanner.imageloading.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.paulrybitskyi.docskanner.imageloading.Target as MyTarget

internal class PicassoTarget(
    private val target: MyTarget
): Target {


    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        target.onBitmapLoadingSucceeded(bitmap)
    }


    override fun onBitmapFailed(exception: Exception, errorDrawable: Drawable?) {
        target.onBitmapLoadingFailed(exception)
    }


    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        target.onPrepareLoad()
    }


}