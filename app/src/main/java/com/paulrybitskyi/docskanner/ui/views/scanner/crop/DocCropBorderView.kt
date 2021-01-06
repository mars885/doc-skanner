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

@file:SuppressLint("NewApi")

package com.paulrybitskyi.docskanner.ui.views.scanner.crop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Magnifier
import androidx.annotation.ColorInt
import com.paulrybitskyi.commons.SdkInfo
import com.paulrybitskyi.commons.ktx.drawing.setBounds
import com.paulrybitskyi.commons.ktx.getColor
import com.paulrybitskyi.commons.ktx.getDimension
import com.paulrybitskyi.commons.ktx.getDimensionPixelSize
import com.paulrybitskyi.commons.utils.observeChanges
import com.paulrybitskyi.docskanner.R
import com.paulrybitskyi.docskanner.imageprocessing.detector.DocCoordsOrderer
import com.paulrybitskyi.docskanner.imageprocessing.detector.DocShape
import com.paulrybitskyi.docskanner.ui.views.scanner.crop.handles.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs


@AndroidEntryPoint
internal class DocCropBorderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val defaultHandleStrokeColor = getColor(R.color.doc_crop_border_handle_stroke_color)
    private val defaultHandleInnerColor = getColor(R.color.doc_crop_border_handle_inner_color)
    private val defaultValidShapeLineColor = getColor(R.color.doc_crop_border_valid_shape_line_color)
    private val defaultInvalidShapeLineColor = getColor(R.color.doc_crop_border_invalid_shape_line_color)
    private val defaultHandleSize = getDimensionPixelSize(R.dimen.doc_crop_border_handle_size)
    private val defaultHandleStrokeWidth = getDimension(R.dimen.doc_crop_border_handle_stroke_width)
    private val defaultShapeLineWidth = getDimension(R.dimen.doc_crop_border_shape_line_width)

    private val handleSizeDeviation = getDimension(R.dimen.doc_crop_border_handle_size_deviation)

    private val isMagnifierEnabled: Boolean
        get() = (SdkInfo.IS_AT_LEAST_PIE && (magnifier != null))

    @get:ColorInt
    private var currentShapeLineColor: Int
        set(@ColorInt color) { shapeLinePaint.color = color }
        get() = shapeLinePaint.color

    private val halfHandleSize: Float
        get() = (handleSize * 0.5f)

    private val handleStrokeCircleRadius: Float
        get() = (halfHandleSize - handleSizeDeviation)

    private val handleInnerCircleRadius: Float
        get() = (((handleSize - handleStrokeWidth) * 0.5f) - handleSizeDeviation)

    private val handleDiameter: Float
        get() = (handleSize - handleSizeDeviation)

    private val docWidth: Float
        get() = (width.toFloat() - handleSize)

    private val docHeight: Float
        get() = (height.toFloat() - handleSize)

    @get:ColorInt
    var handleStrokeColor by observeChanges(defaultHandleStrokeColor) { _, newValue ->
        handleStrokePaint.color = newValue
        invalidate()
    }

    @get:ColorInt
    var handleInnerColor by observeChanges(defaultHandleInnerColor) { _, newValue ->
        handleInnerPaint.color = newValue
        invalidate()
    }

    @get:ColorInt
    var validShapeLineColor by observeChanges(defaultValidShapeLineColor) { _, _ ->
        invalidate()
    }

    @get:ColorInt
    var invalidShapeLineColor by observeChanges(defaultInvalidShapeLineColor) { _, _ ->
        invalidate()
    }

    var handleSize by observeChanges(defaultHandleSize) { _, _ ->
        invalidate()
    }

    var handleStrokeWidth by observeChanges(defaultHandleStrokeWidth) { _, newValue ->
        handleStrokePaint.strokeWidth = newValue
        invalidate()
    }

    var shapeLineWidth by observeChanges(defaultShapeLineWidth) { _, newValue ->
        shapeLinePaint.strokeWidth = newValue
        invalidate()
    }

    private val handleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val handleInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val shapeLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var movingHandle: Handle? = null

    private lateinit var handles: Handles

    private var magnifier: Magnifier? = null

    @Inject lateinit var docCoordsOrderer: DocCoordsOrderer


    init {
        initHandles()
        initMagnifier()
        initDefaults()
    }


    private fun initHandles() {
        val topLeft = EdgeHandle()
        val topRight = EdgeHandle()
        val bottomRight = EdgeHandle()
        val bottomLeft = EdgeHandle()

        handles = Handles(
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft,
            middleLeft = MiddleHandle(topLeft, bottomLeft),
            middleTop = MiddleHandle(topLeft, topRight),
            middleRight = MiddleHandle(topRight, bottomRight),
            middleBottom = MiddleHandle(bottomLeft, bottomRight)
        )
    }


    private fun initMagnifier() {
        if(SdkInfo.IS_AT_LEAST_PIE) {
            magnifier = if(SdkInfo.IS_AT_LEAST_10) {
                Magnifier.Builder(this).build()
            } else {
                Magnifier(this)
            }
        }
    }


    private fun initDefaults() {
        currentShapeLineColor = validShapeLineColor
        handleStrokeColor = handleStrokeColor
        handleInnerColor = handleInnerColor
        handleStrokeWidth = handleStrokeWidth
        shapeLineWidth = shapeLineWidth
    }


    fun setCropBorder(cropBorder: DocCropBorder) = with(handles) {
        topLeft.recalculateBounds(cropBorder.topLeftCoord)
        topRight.recalculateBounds(cropBorder.topRightCoord)
        bottomRight.recalculateBounds(cropBorder.bottomRightCoord)
        bottomLeft.recalculateBounds(cropBorder.bottomLeftCoord)

        recalculateMiddleHandles()
    }


    private fun recalculateMiddleHandles() {
        handles.middleHandlesList().forEach { handle ->
            handle.recalculateBounds(
                calculateCenter(
                    handle.firstEdgeHandle.toPointF(),
                    handle.secondEdgeHandle.toPointF()
                )
            )
        }
    }


    private fun calculateCenter(start: PointF, end: PointF): PointF {
        val centerX = ((start.x + end.x) * 0.5f)
        val centerY = ((start.y + end.y) * 0.5f)

        return PointF(centerX, centerY)
    }


    private fun Handle.recalculateBounds(startPoint: PointF) {
        bounds.setBounds(
            left = startPoint.x,
            top = startPoint.y,
            right = (startPoint.x + handleDiameter),
            bottom = (startPoint.y + handleDiameter)
        )
    }


    fun getCropBorder(): DocCropBorder? {
        return orderBorderCoords()?.let { docShape ->
            DocCropBorder(
                topLeftCoord = docShape.topLeftCoord,
                topRightCoord = docShape.topRightCoord,
                bottomLeftCoord = docShape.bottomLeftCoord,
                bottomRightCoord = docShape.bottomRightCoord
            )
        }
    }


    private fun orderBorderCoords(): DocShape? {
        return docCoordsOrderer.order(
            handles.edgeHandlesList().map(Handle::toPointF)
        )
    }


    fun hasValidBorder(): Boolean {
        return (orderBorderCoords() != null)
    }


    private fun drawMagnifier(x: Float, y: Float) {
        if(isMagnifierEnabled) {
            magnifier?.show(x, y)
        }
    }


    private fun dismissMagnifier()  {
        if(isMagnifierEnabled) {
            magnifier?.dismiss()
        }
    }


    private fun updateCurrentShapeLine() {
        currentShapeLineColor = if(hasValidBorder()) {
            validShapeLineColor
        } else {
            invalidShapeLineColor
        }
    }


    override fun onDraw(canvas: Canvas) = with(canvas) {
        drawHandles()
        drawShapeLines()
    }


    private fun Canvas.drawHandles() {
        val strokeCircleRadius = handleStrokeCircleRadius
        val innerCircleRadius = handleInnerCircleRadius

        var centerX: Float
        var centerY: Float

        for(handle in handles.allHandlesList()) {
            centerX = (handle.x + halfHandleSize)
            centerY = (handle.y + halfHandleSize)

            drawCircle(centerX, centerY, strokeCircleRadius, handleStrokePaint)
            drawCircle(centerX, centerY, innerCircleRadius, handleInnerPaint)
        }
    }


    private fun Canvas.drawShapeLines() {
        drawShapeLine(handles.topLeft.bounds, handles.topRight.bounds)
        drawShapeLine(handles.topRight.bounds, handles.bottomRight.bounds)
        drawShapeLine(handles.bottomLeft.bounds, handles.bottomRight.bounds)
        drawShapeLine(handles.topLeft.bounds, handles.bottomLeft.bounds)
    }


    private fun Canvas.drawShapeLine(start: RectF, end: RectF) {
        drawLine(
            (start.left + halfHandleSize),
            (start.top + halfHandleSize),
            (end.left + halfHandleSize),
            (end.top + halfHandleSize),
            shapeLinePaint
        )
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> onFingerDown(event)
            MotionEvent.ACTION_MOVE -> onFingerMoved(event)

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> onFingerReleased(event)
        }

        return true
    }


    private fun onFingerDown(event: MotionEvent) {
        val handle = handles.allHandlesList().firstOrNull { it.bounds.contains(event.x, event.y) }

        if(handle != null) {
            movingHandle = handle
        }
    }


    private fun onFingerMoved(event: MotionEvent) {
        val movingHandle = (movingHandle ?: return)
        val originalHandleCenterX = movingHandle.bounds.centerX()
        val originalHandleCenterY = movingHandle.bounds.centerY()
        val xDiff = (event.x - movingHandle.x - handleStrokeCircleRadius)
        val yDiff = (event.y - movingHandle.y - handleStrokeCircleRadius)

        when(movingHandle) {
            is EdgeHandle -> onMoveEdgeHandle(movingHandle, xDiff, yDiff)
            is MiddleHandle -> onMoveMiddleHandle(movingHandle, xDiff, yDiff)
        }

        recalculateMiddleHandles()
        updateCurrentShapeLine()
        drawMagnifier(originalHandleCenterX, originalHandleCenterY)
        invalidate()
    }


    private fun onMoveEdgeHandle(handle: EdgeHandle, xDiff: Float, yDiff: Float) {
        val newHandleX = (handle.x + xDiff).coerceIn(0f, docWidth)
        val newHandleY = (handle.y + yDiff).coerceIn(0f, docHeight)
        val newStartPoint = PointF(newHandleX, newHandleY)

        handle.recalculateBounds(newStartPoint)
    }


    private fun onMoveMiddleHandle(handle: MiddleHandle, xDiff: Float, yDiff: Float) {
        val edgeHandlesXDiff = abs(handle.firstEdgeHandle.x - handle.secondEdgeHandle.x)
        val edgeHandlesYDiff = abs(handle.firstEdgeHandle.y - handle.secondEdgeHandle.y)
        val supportsVerticalMovement = (edgeHandlesXDiff > edgeHandlesYDiff)

        val firstEdgeHandleNewStartPoint: PointF
        val secondEdgeHandleNewStartPoint: PointF

        if(supportsVerticalMovement) {
            val newFirstEdgeHandleY = (handle.firstEdgeHandle.y + yDiff).coerceIn(0f, docHeight)
            val newSecondEdgeHandleY = (handle.secondEdgeHandle.y + yDiff).coerceIn(0f, docHeight)

            firstEdgeHandleNewStartPoint = PointF(handle.firstEdgeHandle.x, newFirstEdgeHandleY)
            secondEdgeHandleNewStartPoint = PointF(handle.secondEdgeHandle.x, newSecondEdgeHandleY)
        } else {
            val newFirstEdgeHandleX = (handle.firstEdgeHandle.x + xDiff).coerceIn(0f, docWidth)
            val newSecondEdgeHandleX = (handle.secondEdgeHandle.x + xDiff).coerceIn(0f, docWidth)

            firstEdgeHandleNewStartPoint = PointF(newFirstEdgeHandleX, handle.firstEdgeHandle.y)
            secondEdgeHandleNewStartPoint = PointF(newSecondEdgeHandleX, handle.secondEdgeHandle.y)
        }

        handle.firstEdgeHandle.recalculateBounds(firstEdgeHandleNewStartPoint)
        handle.secondEdgeHandle.recalculateBounds(secondEdgeHandleNewStartPoint)
    }


    private fun onFingerReleased(event: MotionEvent) {
        movingHandle = null
        dismissMagnifier()
    }


}