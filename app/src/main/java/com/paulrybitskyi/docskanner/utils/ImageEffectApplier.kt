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

package com.paulrybitskyi.docskanner.utils

import android.graphics.Bitmap
import com.paulrybitskyi.docskanner.utils.extensions.toBitmap
import com.paulrybitskyi.docskanner.utils.extensions.toMat
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


internal interface ImageEffectApplier {

    fun applyMagicColorEffect(source: Bitmap): Bitmap

    fun applyGrayscaleEffect(source: Bitmap): Bitmap

    fun applyBinaryEffect(source: Bitmap): Bitmap

}


internal class OpenCvImageEffectApplier : ImageEffectApplier {


    private companion object {

        private const val MAGIC_COLOR_EFFECT_CONTRAST = 1.25
        private const val MAGIC_COLOR_EFFECT_BRIGHTNESS = 0.0

        private const val BINARY_EFFECT_THRESHOLD = 127.5
        private const val BINARY_EFFECT_MAX_VALUE = 255.0

    }


    override fun applyMagicColorEffect(source: Bitmap): Bitmap {
        return applyEffect(source) { inputMat, outputMat ->
            inputMat.convertTo(
                outputMat,
                -1,
                MAGIC_COLOR_EFFECT_CONTRAST,
                MAGIC_COLOR_EFFECT_BRIGHTNESS
            )
        }
    }


    override fun applyGrayscaleEffect(source: Bitmap): Bitmap {
        return applyEffect(source) { inputMat, outputMat ->
            Imgproc.cvtColor(inputMat, outputMat, Imgproc.COLOR_BGR2GRAY)
        }
    }


    override fun applyBinaryEffect(source: Bitmap): Bitmap {
        val grayscaleBitmap = applyGrayscaleEffect(source)
        val binaryBitmap = applyEffect(grayscaleBitmap) { inputMat, outputMat ->
            Imgproc.threshold(
                inputMat,
                outputMat,
                BINARY_EFFECT_THRESHOLD,
                BINARY_EFFECT_MAX_VALUE,
                Imgproc.THRESH_BINARY
            )
        }

        if(binaryBitmap != grayscaleBitmap) {
            grayscaleBitmap.recycle()
        }

        return binaryBitmap
    }


    private fun applyEffect(source: Bitmap, action: (Mat, Mat) -> Unit): Bitmap {
        val inputMat = source.toMat()
        val outputMat = Mat(inputMat.rows(), inputMat.cols(), inputMat.type())

        action(inputMat, outputMat)

        val outputBitmap = outputMat.toBitmap()

        inputMat.release()
        outputMat.release()

        return outputBitmap
    }


}