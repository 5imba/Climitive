package com.wildraion.climitive.common

import android.util.Log
import androidx.lifecycle.MutableLiveData

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

// Set default value for any type of MutableLiveData
fun <T :Any?> MutableLiveData<T>.default(initialData: T) = apply { setValue(initialData) }
// Set new value for any type of MutableLiveData
fun <T> MutableLiveData<T>.set(newValue: T) = apply { setValue(newValue) }