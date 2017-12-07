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

        @SuppressLint("SimpleDateFormat")
        fun getDate(fromLong: Long): Date {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = fromLong
            return Date(formatter.format(calendar.time))
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