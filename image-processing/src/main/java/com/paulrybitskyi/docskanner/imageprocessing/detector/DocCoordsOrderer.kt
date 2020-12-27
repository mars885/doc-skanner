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

package com.paulrybitskyi.docskanner.imageprocessing.detector

import android.graphics.PointF
import javax.inject.Inject


interface DocCoordsOrderer {

    fun order(coords: List<PointF>): DocShape?

}


internal class DocCoordsOrdererImpl @Inject constructor() : DocCoordsOrderer {


    internal companion object {

        val STUB_COORD = PointF(-1f, -1f)

    }


    override fun order(coords: List<PointF>): DocShape? {
        val centerCoord = calculateCenterCoord(coords)

        var topLeftCoord = STUB_COORD
        var topRightCoord = STUB_COORD
        var bottomLeftCoord = STUB_COORD
        var bottomRightCoord = STUB_COORD

        for(coord in coords) {
            topLeftCoord = (if(isTopLeftCoord(coord, centerCoord)) coord else topLeftCoord)
            topRightCoord = (if(isTopRightCoord(coord, centerCoord)) coord else topRightCoord)
            bottomLeftCoord = (if(isBottomLeftCoord(coord, centerCoord)) coord else bottomLeftCoord)
            bottomRightCoord = (if(isBottomRightCoord(coord, centerCoord)) coord else bottomRightCoord)
        }

        if((topLeftCoord == STUB_COORD) ||
            (topRightCoord == STUB_COORD) ||
            (bottomLeftCoord == STUB_COORD) ||
            (bottomRightCoord == STUB_COORD)) {
            return null
        }

        return DocShape(
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


    private fun isTopLeftCoord(coord: PointF, centerCoord: PointF): Boolean {
        return ((coord.x < centerCoord.x) && (coord.y < centerCoord.y))
    }


    private fun isTopRightCoord(coord: PointF, centerCoord: PointF): Boolean {
        return ((coord.x > centerCoord.x) && (coord.y < centerCoord.y))
    }


    private fun isBottomLeftCoord(coord: PointF, centerCoord: PointF): Boolean {
        return ((coord.x < centerCoord.x) && (coord.y > centerCoord.y))
    }


    private fun isBottomRightCoord(coord: PointF, centerCoord: PointF): Boolean {
        return ((coord.x > centerCoord.x) && (coord.y > centerCoord.y))
    }


}