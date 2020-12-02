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

package com.paulrybitskyi.docskanner.utils.extensions

import android.os.FileObserver
import com.paulrybitskyi.commons.SdkInfo
import java.io.File


internal inline fun File.newFileObserver(
    events: Int = FileObserver.ALL_EVENTS,
    crossinline onEventListener: (Int, String?) -> Unit
): FileObserver {
    return if(SdkInfo.IS_AT_LEAST_10) {
        object : FileObserver(this, events) {
            override fun onEvent(event: Int, path: String?) = onEventListener(event, path)
        }
    } else {
        object : FileObserver(this.absolutePath, events) {
            override fun onEvent(event: Int, path: String?) = onEventListener(event, path)
        }
    }
}


internal fun File.fileList(): List<File> {
    if(!isDirectory) return emptyList()

    return (listFiles()?.toList() ?: emptyList())
}