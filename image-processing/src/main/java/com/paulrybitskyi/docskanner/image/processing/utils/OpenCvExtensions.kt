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

package com.paulrybitskyi.docskanner.image.processing.utils

import android.graphics.Bitmap
import android.graphics.PointF
import org.opencv.android.Utils
import org.opencv.core.*


internal fun PointF.toPoint(): Point {
    return Point(x.toDouble(), y.toDouble())
}


internal fun Point.toPointF(): PointF {
    return PointF(x.toFloat(), y.toFloat())
}


internal fun MatOfPoint2f.toMatOfPoint(): MatOfPoint {
    return MatOfPoint().also { convertTo(it, CvType.CV_32S) }
}


internal fun MatOfPoint.toMatOfPoint2f(): MatOfPoint2f {
    return MatOfPoint2f().also { convertTo(it, CvType.CV_32FC2) }
}


internal fun Bitmap.toMat(): Mat {
    return Mat().also { Utils.bitmapToMat(this, it) }
}


internal fun Mat.toBitmap(): Bitmap {
    return Bitmap.createBitmap(
        width(),
        height(),
        Bitmap.Config.ARGB_8888
    )
    .also { Utils.matToBitmap(this, it) }
}