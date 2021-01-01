/*
 * Copyright 2021 Paul Rybitskyi, paul.rybitskyi.work@gmail.com
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

package com.paulrybitskyi.docskanner.ui.views.scanner.crop.handles

internal class Handles(
    val topLeft: EdgeHandle,
    val topRight: EdgeHandle,
    val bottomRight: EdgeHandle,
    val bottomLeft: EdgeHandle,
    val middleLeft: MiddleHandle,
    val middleTop: MiddleHandle,
    val middleRight: MiddleHandle,
    val middleBottom: MiddleHandle
) {


    fun allHandlesList(): List<Handle> {
        return buildList {
            addAll(edgeHandlesList())
            addAll(middleHandlesList())
        }
    }


    fun edgeHandlesList(): List<EdgeHandle> {
        return listOf(topLeft, topRight, bottomRight, bottomLeft)
    }


    fun middleHandlesList(): List<MiddleHandle> {
        return listOf(middleLeft, middleTop, middleRight, middleBottom)
    }


}