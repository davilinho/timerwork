package com.davidroid.worktimer.dateUtil

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*



/**
* Created by davidmartin on 5/12/17.
*/
class DateUtil {

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getSimpleDate(fromDate: Date): Date {
            return SimpleDateFormat("dd/MM/yyyy").parse(SimpleDateFormat("dd/MM/yyyy").format(fromDate))
        }

        fun getHoursAndMinutes(from: Long): String {
            val totalSecs = from / 1000
            val hours = totalSecs / 3600
            val minutes = totalSecs / 60 % 60
            val secs = totalSecs % 60
            val minutesString = if (minutes == 0L) "00" else if (minutes < 10) "0$minutes" else "$minutes"
            val secsString = if (secs == 0L) "00" else if (secs < 10) "0$secs" else "$secs"
            return when {
                hours > 0 -> "$hours:$minutesString:$secsString"
                minutes > 0 -> "00:$minutesString:$secsString"
                else -> "00:00:$secsString"
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentTime(): String {
            return SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentDay(): String {
            return SimpleDateFormat("dd/MM/yyyy").format(Date())
        }
    }
}