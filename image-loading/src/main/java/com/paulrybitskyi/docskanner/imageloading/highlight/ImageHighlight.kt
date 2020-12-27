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

package com.paulrybitskyi.docskanner.imageloading.highlight

import android.graphics.PointF


data class ImageHighlight(
    val topLeftCoord: PointF,
    val topRightCoord: PointF,
    val bottomLeftCoord: PointF,
    val bottomRightCoord: PointF
) {


    internal companion object {

         val STUB_COORD = PointF(-1f, -1f)

    }


    val isValid: Boolean
        get() = (
            (topLeftCoord != STUB_COORD) &&
            (topRightCoord != STUB_COORD) &&
            (bottomLeftCoord != STUB_COORD) &&
            (bottomRightCoord != STUB_COORD)
        )


}


object ImageHighlightFactory {


    fun fromCoordinates(coords: List<PointF>): ImageHighlight {
        val centerCoord = calculateCenterCoord(coords)

        fun isTopLeftCoord(coord: PointF): Boolean {
            return ((coord.x < centerCoord.x) && (coord.y < centerCoord.y))
        }

        fun isTopRightCoord(coord: PointF): Boolean {
            return ((coord.x > centerCoord.x) && (coord.y < centerCoord.y))
        }

        fun isBottomLeftCoord(coord: PointF): Boolean {
            return ((coord.x < centerCoord.x) && (coord.y > centerCoord.y))
        }

        fun isBottomRightCoord(coord: PointF): Boolean {
            return ((coord.x > centerCoord.x) && (coord.y > centerCoord.y))
        }

        var topLeftCoord = ImageHighlight.STUB_COORD
        var topRightCoord = ImageHighlight.STUB_COORD
        var bottomLeftCoord = ImageHighlight.STUB_COORD
        var bottomRightCoord = ImageHighlight.STUB_COORD

        for(coord in coords) {
            topLeftCoord = (if(isTopLeftCoord(coord)) coord else topLeftCoord)
            topRightCoord = (if(isTopRightCoord(coord)) coord else topRightCoord)
            bottomLeftCoord = (if(isBottomLeftCoord(coord)) coord else bottomLeftCoord)
            bottomRightCoord = (if(isBottomRightCoord(coord)) coord else bottomRightCoord)
        }

        return ImageHighlight(
            topLeftCoord = topLeftCoord,
            topRightCoord = topRightCoord,
            bottomLeftCoord = bottomLeftCoord,
            bottomRightCoord = bottomRightCoord
        )
    }


    private fun calculateCenterCoord(coords: List<PointF>): PointF {
        val centerCoord = PointF()
        val coordCount = coords.size

        for(coord in coords) {
            centerCoord.x += (coord.x / coordCount)
            centerCoord.y += (coord.y / coordCount)
        }

        return centerCoord
    }


}