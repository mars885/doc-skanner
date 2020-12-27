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

package com.paulrybitskyi.docskanner.core.providers

import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


interface CoroutineScopeProvider {

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job

    fun <T> async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ): Deferred<T>

    fun cancelChildren()

}


internal class CoroutineScopeProviderImpl @Inject constructor() : CoroutineScopeProvider {


    private val scope = CoroutineScope(SupervisorJob())


    override fun launch(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return scope.launch(context, start, block)
    }


    override fun <T> async(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> T
    ): Deferred<T> {
        return scope.async(context, start, block)
    }


    override fun cancelChildren() {
        scope.coroutineContext.cancelChildren()
    }


}