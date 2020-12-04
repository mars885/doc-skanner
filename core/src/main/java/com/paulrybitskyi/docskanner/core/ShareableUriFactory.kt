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

package com.paulrybitskyi.docskanner.core

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


interface ShareableUriFactory {

    fun createShareableUri(file: File): Uri

}


internal class ShareableUriFactoryImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : ShareableUriFactory {


    private companion object {

        private const val AUTHORITY = "com.paulrybitskyi.docskanner.provider"

    }


    override fun createShareableUri(file: File): Uri {
        return FileProvider.getUriForFile(applicationContext, AUTHORITY, file)
    }


}