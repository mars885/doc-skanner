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

package com.paulrybitskyi.docskanner.core.verifiers

import android.content.Context
import android.content.pm.PackageManager
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


interface CameraPresenceVerifier {

    fun isCameraPresent(): Boolean

}


@BindType
internal class CameraPresenceVerifierImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : CameraPresenceVerifier {


    override fun isCameraPresent(): Boolean {
        return applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }


}