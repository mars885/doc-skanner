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

package com.paulrybitskyi.docscanner.utils.extensions

import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


internal inline fun DexterBuilder.SinglePermissionListener.withListener(
    crossinline onPermissionGranted: () -> Unit = {},
    crossinline onPermissionDenied: () -> Unit = {},
    crossinline onPermissionRationaleShouldBeShown: () -> Unit = {}
): DexterBuilder {
    return object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse) = onPermissionGranted()
        override fun onPermissionDenied(response: PermissionDeniedResponse) = onPermissionDenied()
        override fun onPermissionRationaleShouldBeShown(
            request: PermissionRequest,
            token: PermissionToken
        ) {
            token.continuePermissionRequest()
            onPermissionRationaleShouldBeShown()
        }
    }
    .let(::withListener)
}