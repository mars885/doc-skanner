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

package com.paulrybitskyi.docscanner.ui.views.imagecropper

import android.graphics.PointF

internal data class CroppingCoords(
    val topLeftCoord: PointF,
    val topRightCoord: PointF,
    val bottomLeftCoord: PointF,
    val bottomRightCoord: PointF
)


internal fun CroppingCoords.toList(): List<PointF> {
    return listOf(
        topLeftCoord,
        topRightCoord,
        bottomLeftCoord,
        bottomRightCoord
    )
}


internal fun fromList(coords: List<PointF>): CroppingCoords {
    return CroppingCoords(
        topLeftCoord = coords[0],
        topRightCoord = coords[1],
        bottomLeftCoord = coords[2],
        bottomRightCoord = coords[3]
    )
}