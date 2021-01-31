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

package com.paulrybitskyi.docskanner.core.formatters

import android.content.Context
import com.paulrybitskyi.hiltbinder.BindType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


interface DocDateFormatter {

    fun formatDate(timestamp: Long): String?

}


@BindType
internal class DocDateFormatterImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : DocDateFormatter {


    private companion object {

        private const val FORMATTING_PATTERN_WITH_12_HOUR_FORMAT = "dd.MM.yyyy hh:mm a"
        private const val FORMATTING_PATTERN_WITH_24_HOUR_FORMAT = "dd.MM.yyyy HH:mm"

    }


    override fun formatDate(timestamp: Long): String? {
        if(timestamp == 0L) return null

        val formattingPattern = getFormattingPattern()
        val formatter = SimpleDateFormat(formattingPattern, Locale.getDefault())
        val formattedDate = formatter.format(Date(timestamp))

        return formattedDate
    }


    private fun getFormattingPattern(): String {
        return if(is24HourFormat(applicationContext)) {
            FORMATTING_PATTERN_WITH_24_HOUR_FORMAT
        } else {
            FORMATTING_PATTERN_WITH_12_HOUR_FORMAT
        }
    }


    private fun is24HourFormat(context: Context): Boolean {
        return android.text.format.DateFormat.is24HourFormat(context)
    }


}