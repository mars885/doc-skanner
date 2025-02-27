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

package com.paulrybitskyi.docskanner.utils.dialogs

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent


internal fun Dialog.show(lifecycleOwner: LifecycleOwner) = apply {
    show(lifecycleOwner.lifecycle)
}


internal fun Dialog.show(lifecycle: Lifecycle) = apply {
    DialogDismisser(lifecycle) { dismiss() }
    show()
}


private class DialogDismisser(
    lifecycle: Lifecycle,
    private val onDismiss: () -> Unit
) : LifecycleObserver {


    init {
        lifecycle.addObserver(this)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onPause() {
        onDismiss()
    }


}