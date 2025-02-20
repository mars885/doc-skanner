/*
 * Copyright 2021 Paul Rybitskyi, oss@paulrybitskyi.com
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

import android.graphics.PointF
import android.graphics.RectF


internal interface Handle {

    val bounds: RectF

}


internal val Handle.x: Float
    get() = bounds.left

internal val Handle.y: Float
    get() = bounds.top


internal fun Handle.toPointF(): PointF {
    return PointF(x, y)
}