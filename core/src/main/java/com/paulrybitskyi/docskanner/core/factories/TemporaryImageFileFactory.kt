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

package com.paulrybitskyi.docskanner.core.factories

import android.content.Context
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


interface TemporaryImageFileFactory {

    fun createTempImageFile(): File

}


@BindType
internal class TemporaryImageFileFactoryImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : TemporaryImageFileFactory {


    override fun createTempImageFile(): File {
        return File.createTempFile("IMG_", ".jpg", applicationContext.cacheDir)
    }


}