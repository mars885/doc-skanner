package com.paulrybitskyi.docskanner.ui.views.scanner.crop

import android.graphics.PointF

internal data class DocCropBorder(
    val topLeftCoord: PointF,
    val topRightCoord: PointF,
    val bottomLeftCoord: PointF,
    val bottomRightCoord: PointF
)