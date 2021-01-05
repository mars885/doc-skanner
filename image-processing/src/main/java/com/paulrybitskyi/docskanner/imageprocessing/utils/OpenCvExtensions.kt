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

package com.paulrybitskyi.docskanner.imageprocessing.utils

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.annotation.FloatRange
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


internal fun Mat.createCopy(): Mat {
    return Mat(rows(), cols(), type())
}


internal fun Mat.toBitmap(): Bitmap {
    return Bitmap.createBitmap(
        width(),
        height(),
        Bitmap.Config.ARGB_8888
    )
    .also { Utils.matToBitmap(this, it) }
}


internal fun Mat.applyGrayscaleEffect(): Mat {
    return createCopy().also { outputMat ->
        Imgproc.cvtColor(this, outputMat, Imgproc.COLOR_BGR2GRAY)
    }
}


internal fun Mat.applyGaussianBlur(
    kernelSeize: Double,
    sigmaX: Double = 0.0
): Mat {
    return createCopy().also { outputMat ->
        Imgproc.GaussianBlur(
            this,
            outputMat,
            Size(kernelSeize, kernelSeize),
            sigmaX
        )
    }
}


internal fun Mat.findCannyEdges(
    minThreshold: Double,
    maxThreshold: Double
): Mat {
    return createCopy().also { edgesMat ->
        Imgproc.Canny(this, edgesMat, minThreshold, maxThreshold)
    }
}


internal fun Mat.findContours(
    hierarchy: Mat = Mat(),
    mode: Int = Imgproc.RETR_LIST,
    method: Int = Imgproc.CHAIN_APPROX_SIMPLE
): List<MatOfPoint> {
    return mutableListOf<MatOfPoint>().also { contoursMat ->
        Imgproc.findContours(
            this,
            contoursMat,
            hierarchy,
            mode,
            method
        )
    }
}


internal fun MatOfPoint.approxPolyCurve(
    @FloatRange(from = 0.0, to = 100.0)
    accuracyPercentage: Double,
    isCurveClosed: Boolean
): MatOfPoint2f {
    val contourMatFloat = toMatOfPoint2f()
    val contourPerimeter = Imgproc.arcLength(contourMatFloat, isCurveClosed)
    val epsilon = (contourPerimeter * accuracyPercentage / 100.0)

    return MatOfPoint2f()
        .also { approximateCurve ->
            Imgproc.approxPolyDP(
                contourMatFloat,
                approximateCurve,
                epsilon,
                isCurveClosed
            )
        }
}


internal fun PointF.toPoint(): Point {
    return Point(x.toDouble(), y.toDouble())
}


internal fun Point.toPointF(): PointF {
    return PointF(x.toFloat(), y.toFloat())
}


internal fun MatOfPoint.toMatOfPoint2f(): MatOfPoint2f {
    return MatOfPoint2f().also { convertTo(it, CvType.CV_32FC2) }
}


internal fun Bitmap.toMat(): Mat {
    return Mat().also { Utils.bitmapToMat(this, it) }
}