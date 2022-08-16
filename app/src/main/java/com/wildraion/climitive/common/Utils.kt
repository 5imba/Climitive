package com.wildraion.climitive.common

import android.util.Log

object Utils {

    fun parseTimeFromData(data: String): String {
        try {
            val dateList = data.split(' ')
            val timeList = dateList[1].split(':')
            return timeList[0]
        } catch (e: Exception) {
            Log.e("ParseTimeFromData", "Error: ${e.localizedMessage}")
        }
        return ""
    }

    fun temperatureToPretty(temp: Double): String {
        return "${temp.toInt()}°"
    }
}