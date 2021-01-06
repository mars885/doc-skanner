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

package com.paulrybitskyi.docskanner.imageprocessing.effects

import android.graphics.Bitmap
import com.paulrybitskyi.docskanner.imageprocessing.utils.*
import com.paulrybitskyi.docskanner.imageprocessing.utils.applyGrayscaleEffect
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import javax.inject.Inject

internal class ImageEffectApplier @Inject constructor() {


    private companion object {

        private const val THRESHOLD_MAX_VALUE = 255.0

        private const val SIMPLE_THRESHOLD_VALUE = 127.5
        private const val ADAPTIVE_THRESHOLD_BLOCK_SIZE = 55
        private const val ADAPTIVE_THRESHOLD_CONSTANT = 15.0

    }


    fun applyGrayscaleEffect(source: Bitmap): Bitmap {
        return applyEffect(source, Mat::applyGrayscaleEffect)
    }


    fun applySimpleThresholdEffect(source: Bitmap): Bitmap {
        val grayscaleBitmap = applyGrayscaleEffect(source)
        val binaryBitmap = applyEffect(grayscaleBitmap) { inputMat ->
            inputMat.applySimpleThreshold(
                SIMPLE_THRESHOLD_VALUE,
                THRESHOLD_MAX_VALUE,
                Imgproc.THRESH_BINARY
            )
        }

        if(binaryBitmap != grayscaleBitmap) {
            grayscaleBitmap.recycle()
        }

        return binaryBitmap
    }


    fun applyAdaptiveThresholdEffect(source: Bitmap): Bitmap {
        val sourceMat = source.toMat()
        val supposedlyGrayscaleMat = sourceMat.applyGrayscaleEffect()

        sourceMat.release()

        val grayscaleMat = supposedlyGrayscaleMat.createCopy(type = CvType.CV_8UC1)
            .also { supposedlyGrayscaleMat.assignTo(it, CvType.CV_8UC1) }

        supposedlyGrayscaleMat.release()

        val binaryMat = grayscaleMat.applyAdaptiveThreshold(
            maxValue = THRESHOLD_MAX_VALUE,
            adaptiveMethod = Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            thresholdType = Imgproc.THRESH_BINARY,
            blockSize = ADAPTIVE_THRESHOLD_BLOCK_SIZE,
            C = ADAPTIVE_THRESHOLD_CONSTANT
        )

        grayscaleMat.release()

        val binaryBitmap = binaryMat.toBitmap()

        binaryMat.release()

        return binaryBitmap
    }


    private fun applyEffect(source: Bitmap, action: (Mat) -> Mat): Bitmap {
        val inputMat = source.toMat()
        val outputMat = action(inputMat)
        val outputBitmap = outputMat.toBitmap()

        inputMat.release()
        outputMat.release()

        return outputBitmap
    }


}