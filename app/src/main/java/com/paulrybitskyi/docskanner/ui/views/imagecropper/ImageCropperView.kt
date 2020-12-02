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

package com.paulrybitskyi.docskanner.ui.views.imagecropper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Magnifier
import androidx.appcompat.widget.AppCompatImageView
import com.paulrybitskyi.commons.SdkInfo
import com.paulrybitskyi.commons.ktx.getColor
import com.paulrybitskyi.commons.ktx.getDimension
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.utils.highlight.ImageHighlight
import com.paulrybitskyi.docskanner.utils.highlight.ImageHighlightFactory
import com.paulrybitskyi.docskanner.utils.highlight.ImageHighlightFinder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility", "NewApi")
internal class ImageCropperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {


    private companion object {

        private const val MAGNIFIER_OFFSET = 50

    }


    private val validShapeLineColor = getColor(R.color.image_cropper_valid_shape_line_color)
    private val invalidShapeLineColor = getColor(R.color.image_cropper_invalid_shape_line_color)

    private val lineStrokeWidth = getDimension(R.dimen.image_cropper_line_stroke_width)
    
    private val isMagnifierEnabled: Boolean
        get() = (SdkInfo.IS_AT_LEAST_PIE && (magnifier != null))

    private var lineColor: Int
        set(value) { linePaint.color = value }
        get() = linePaint.color

    private lateinit var topLeftPointerIv: AppCompatImageView
    private lateinit var topRightPointerIv: AppCompatImageView
    private lateinit var bottomLeftPointerIv: AppCompatImageView
    private lateinit var bottomRightPointerIv: AppCompatImageView

    private lateinit var middleLeftPointerIv: AppCompatImageView
    private lateinit var middleTopPointerIv: AppCompatImageView
    private lateinit var middleBottomPointerIv: AppCompatImageView
    private lateinit var middleRightPointerIv: AppCompatImageView

    private lateinit var linePaint: Paint

    private var magnifier: Magnifier? = null

    @Inject lateinit var imageHighlightFinder: ImageHighlightFinder


    init {
        initMagnifier()
        initPointers(context)
        initLinePaint()
    }



    private fun initMagnifier() {
        if(SdkInfo.IS_AT_LEAST_PIE) {
            magnifier = Magnifier.Builder(this).build()
        }
    }


    private fun initPointers(context: Context) {
        initEdgePointers(context)
        initMiddlePointers(context)
    }


    private fun initEdgePointers(context: Context) {
        topLeftPointerIv = initImageView(context)
        topLeftPointerIv.setOnTouchListener(EdgePointerTouchListener())

        topRightPointerIv = initImageView(context)
        topRightPointerIv.setOnTouchListener(EdgePointerTouchListener())

        bottomLeftPointerIv = initImageView(context)
        bottomLeftPointerIv.setOnTouchListener(EdgePointerTouchListener())

        bottomRightPointerIv = initImageView(context)
        bottomRightPointerIv.setOnTouchListener(EdgePointerTouchListener())

        addView(topLeftPointerIv)
        addView(topRightPointerIv)
        addView(bottomLeftPointerIv)
        addView(bottomRightPointerIv)
    }


    private fun initMiddlePointers(context: Context) {
        middleLeftPointerIv = initImageView(context)
        middleLeftPointerIv.setOnTouchListener(MiddlePointerTouchListener(topLeftPointerIv, bottomLeftPointerIv))

        middleTopPointerIv = initImageView(context)
        middleTopPointerIv.setOnTouchListener(MiddlePointerTouchListener(topLeftPointerIv, topRightPointerIv))

        middleBottomPointerIv = initImageView(context)
        middleBottomPointerIv.setOnTouchListener(MiddlePointerTouchListener(bottomLeftPointerIv, bottomRightPointerIv))

        middleRightPointerIv = initImageView(context)
        middleRightPointerIv.setOnTouchListener(MiddlePointerTouchListener(topRightPointerIv, bottomRightPointerIv))

        addView(middleLeftPointerIv)
        addView(middleTopPointerIv)
        addView(middleBottomPointerIv)
        addView(middleRightPointerIv)
    }


    private fun initImageView(context: Context): AppCompatImageView {
        return AppCompatImageView(context)
            .apply {
                layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                setImageResource(R.drawable.image_cropper_pointer)
            }
    }


    private fun initLinePaint() {
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = validShapeLineColor
                strokeWidth = lineStrokeWidth
            }
    }


    fun setImage(image: Bitmap) {
        setImageHighlight(imageHighlightFinder.findImageHighlight(image))
    }


    private fun setImageHighlight(imageHighlight: ImageHighlight) {
        if(!imageHighlight.isValid) return

        topLeftPointerIv.x = imageHighlight.topLeftCoord.x
        topLeftPointerIv.y = imageHighlight.topLeftCoord.y

        topRightPointerIv.x = imageHighlight.topRightCoord.x
        topRightPointerIv.y = imageHighlight.topRightCoord.y

        bottomLeftPointerIv.x = imageHighlight.bottomLeftCoord.x
        bottomLeftPointerIv.y = imageHighlight.bottomLeftCoord.y

        bottomRightPointerIv.x = imageHighlight.bottomRightCoord.x
        bottomRightPointerIv.y = imageHighlight.bottomRightCoord.y
    }


    fun getCroppingCoords(): CroppingCoords {
        val imageHighlight = getImageHighlight()

        return CroppingCoords(
            topLeftCoord = imageHighlight.topLeftCoord,
            topRightCoord = imageHighlight.topRightCoord,
            bottomLeftCoord = imageHighlight.bottomLeftCoord,
            bottomRightCoord = imageHighlight.bottomRightCoord
        )
    }


    fun hasValidCroppingCoords(): Boolean {
        return getImageHighlight().isValid
    }


    private fun getImageHighlight(): ImageHighlight {
        val coords = buildList {
            add(PointF(topLeftPointerIv.x, topLeftPointerIv.y))
            add(PointF(topRightPointerIv.x, topRightPointerIv.y))
            add(PointF(bottomLeftPointerIv.x, bottomLeftPointerIv.y))
            add(PointF(bottomRightPointerIv.x, bottomRightPointerIv.y))
        }

        return ImageHighlightFactory.fromCoordinates(coords)
    }


    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        canvas.drawLines()

        recalculateMiddlePointersPositions()
    }
    
    
    private fun Canvas.drawLines() {
        drawLine(
            (topLeftPointerIv.x + (topLeftPointerIv.width / 2f)),
            (topLeftPointerIv.y + (topLeftPointerIv.height / 2f)),
            (bottomLeftPointerIv.x + (bottomLeftPointerIv.width / 2f)),
            (bottomLeftPointerIv.y + (bottomLeftPointerIv.height / 2f)),
            linePaint
        )

        drawLine(
            (topLeftPointerIv.x + (topLeftPointerIv.width / 2f)),
            (topLeftPointerIv.y + (topLeftPointerIv.height / 2f)),
            (topRightPointerIv.x + (topRightPointerIv.width / 2f)),
            (topRightPointerIv.y + (topRightPointerIv.height / 2f)),
            linePaint
        )

        drawLine(
            (topRightPointerIv.x + (topRightPointerIv.width / 2f)),
            (topRightPointerIv.y + (topRightPointerIv.height / 2f)),
            (bottomRightPointerIv.x + (bottomRightPointerIv.width / 2f)),
            (bottomRightPointerIv.y + (bottomRightPointerIv.height / 2f)),
            linePaint
        )

        drawLine(
            (bottomLeftPointerIv.x + (bottomLeftPointerIv.width / 2f)),
            (bottomLeftPointerIv.y + (bottomLeftPointerIv.height / 2f)),
            (bottomRightPointerIv.x + (bottomRightPointerIv.width / 2f)),
            (bottomRightPointerIv.y + (bottomRightPointerIv.height / 2f)),
            linePaint
        )
    }


    private fun recalculateMiddlePointersPositions() {
        middleLeftPointerIv.x = (bottomLeftPointerIv.x - ((bottomLeftPointerIv.x - topLeftPointerIv.x) / 2f))
        middleLeftPointerIv.y = (bottomLeftPointerIv.y - ((bottomLeftPointerIv.y - topLeftPointerIv.y) / 2f))

        middleRightPointerIv.x = (bottomRightPointerIv.x - ((bottomRightPointerIv.x - topRightPointerIv.x) / 2f))
        middleRightPointerIv.y = (bottomRightPointerIv.y - ((bottomRightPointerIv.y - topRightPointerIv.y) / 2f))

        middleTopPointerIv.x = (topRightPointerIv.x - ((topRightPointerIv.x - topLeftPointerIv.x) / 2f))
        middleTopPointerIv.y = (topRightPointerIv.y - ((topRightPointerIv.y - topLeftPointerIv.y) / 2f))

        middleBottomPointerIv.x = (bottomRightPointerIv.x - ((bottomRightPointerIv.x - bottomLeftPointerIv.x) / 2f))
        middleBottomPointerIv.y = (bottomRightPointerIv.y - ((bottomRightPointerIv.y - bottomLeftPointerIv.y) / 2f))
    }


    private fun drawMagnifier(x: Float, y: Float) {
        if(isMagnifierEnabled) {
            magnifier?.show((x + MAGNIFIER_OFFSET), (y + MAGNIFIER_OFFSET))
        }
    }


    private fun dismissMagnifier()  {
        if(isMagnifierEnabled) magnifier?.dismiss()
    }


    private fun getCurrentShapeLineColor(): Int {
        return if(hasValidCroppingCoords()) {
            validShapeLineColor
        } else {
            invalidShapeLineColor
        }
    }


    private inner class EdgePointerTouchListener : OnTouchListener {

        private val originalPoint = PointF()
        private var pointerViewCoords = PointF()

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    originalPoint.x = event.x
                    originalPoint.y = event.y
                    pointerViewCoords = PointF(view.x, view.y)
                }

                MotionEvent.ACTION_MOVE -> {
                    val currentPoint = PointF((event.x - originalPoint.x), (event.y - originalPoint.y))
                    val isWithinHorizontalBounds = (
                        ((pointerViewCoords.x + currentPoint.x) > 0) &&
                        ((pointerViewCoords.x + currentPoint.x + view.width) < width)
                    )
                    val isWithinVerticalBounds = (
                        (pointerViewCoords.y + currentPoint.y) > 0) &&
                        (((pointerViewCoords.y + currentPoint.y + view.height) < height)
                    )

                    if(isWithinHorizontalBounds && isWithinVerticalBounds) {
                        view.x = (pointerViewCoords.x + currentPoint.x)
                        view.y = (pointerViewCoords.y + currentPoint.y)

                        pointerViewCoords = PointF(view.x, view.y)

                        drawMagnifier(pointerViewCoords.x, pointerViewCoords.y)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    lineColor = getCurrentShapeLineColor()
                    dismissMagnifier()
                }

            }

            invalidate()

            return true
        }

    }


    private inner class MiddlePointerTouchListener(
        private val firstEdgePointer: ImageView,
        private val secondEdgePointer: ImageView
    ) : OnTouchListener {

        private val originalPoint = PointF() // Record Mouse Position When Pressed Down
        private var pointerViewCoords = PointF() // Record Start Position of 'img'

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    originalPoint.x = event.x
                    originalPoint.y = event.y
                    pointerViewCoords = PointF(view.x, view.y)
                }

                MotionEvent.ACTION_MOVE -> {
                    val oldViewPosition = PointF(view.x, view.y)
                    val currentPoint = PointF((event.x - originalPoint.x), (event.y - originalPoint.y))

                    if(abs(firstEdgePointer.x - secondEdgePointer.x) > abs(firstEdgePointer.y - secondEdgePointer.y)) {
                        if(view.isWithinVerticalBounds(secondEdgePointer, currentPoint)) {
                            view.x = (pointerViewCoords.y + currentPoint.y)

                            pointerViewCoords = PointF(view.x, view.y)
                            secondEdgePointer.y = (secondEdgePointer.y + currentPoint.y)
                        }

                        if(view.isWithinVerticalBounds(firstEdgePointer, currentPoint)) {
                            view.x = (pointerViewCoords.y + currentPoint.y)

                            pointerViewCoords = PointF(view.x, view.y)
                            firstEdgePointer.y = (firstEdgePointer.y + currentPoint.y)
                        }
                    } else {
                        if(view.isWithinHorizontalBounds(secondEdgePointer, currentPoint)) {
                            view.x = (pointerViewCoords.x + currentPoint.x)

                            pointerViewCoords = PointF(view.x, view.y)
                            secondEdgePointer.x = (secondEdgePointer.x + currentPoint.x)
                        }

                        if(view.isWithinHorizontalBounds(firstEdgePointer, currentPoint)) {
                            view.x = (pointerViewCoords.x + currentPoint.x)

                            pointerViewCoords = PointF(view.x, view.y)
                            firstEdgePointer.x = (firstEdgePointer.x + currentPoint.x)
                        }
                    }

                    drawMagnifier(oldViewPosition.x, oldViewPosition.y)
                }

                MotionEvent.ACTION_UP -> {
                    lineColor = getCurrentShapeLineColor()
                    dismissMagnifier()
                }

            }

            invalidate()

            return true
        }

        private fun View.isWithinHorizontalBounds(edgePointer: View, currentPoint: PointF): Boolean {
            return (
                ((edgePointer.x + currentPoint.x) > 0) &&
                ((edgePointer.x + currentPoint.x + width) < this@ImageCropperView.width)
            )
        }

        private fun View.isWithinVerticalBounds(edgePointer: View, currentPoint: PointF): Boolean {
            return (
                ((edgePointer.y + currentPoint.y) > 0) &&
                ((edgePointer.y + currentPoint.y + height) < this@ImageCropperView.height)
            )
        }

    }


}