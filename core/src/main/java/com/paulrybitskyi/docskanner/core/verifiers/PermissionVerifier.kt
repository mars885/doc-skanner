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
import com.paulrybitskyi.commons.ktx.arePermissionsDenied
import com.paulrybitskyi.commons.ktx.arePermissionsGranted
import com.paulrybitskyi.commons.ktx.isPermissionDenied
import com.paulrybitskyi.commons.ktx.isPermissionGranted
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


interface PermissionVerifier {

    fun isPermissionGranted(permission: String): Boolean

    fun isPermissionDenied(permission: String): Boolean

    fun arePermissionsGranted(permissions: Set<String>): Boolean

    fun arePermissionsDenied(permissions: Set<String>): Boolean

}


@BindType
internal class PermissionVerifierImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : PermissionVerifier {


    override fun isPermissionGranted(permission: String): Boolean {
        return applicationContext.isPermissionGranted(permission)
    }


    override fun isPermissionDenied(permission: String): Boolean {
        return applicationContext.isPermissionDenied(permission)
    }


    override fun arePermissionsGranted(permissions: Set<String>): Boolean {
        return applicationContext.arePermissionsGranted(permissions)
    }


    override fun arePermissionsDenied(permissions: Set<String>): Boolean {
        return applicationContext.arePermissionsDenied(permissions)
    }

}