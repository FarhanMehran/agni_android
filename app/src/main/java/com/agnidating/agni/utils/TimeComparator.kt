package com.agnidating.agni.utils

import java.text.SimpleDateFormat
import java.util.Locale

object TimeComparator {

    fun isNewTimeGreaterThan24Hours(referenceTime: String, newTime: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return try {
            // Parse reference time and new time
            val referenceDate = sdf.parse(referenceTime)
            val newDate = sdf.parse(newTime)

            // Calculate the difference in milliseconds
            val timeDifferenceMillis = newDate.time - referenceDate.time

            // Calculate 24 hours in milliseconds
            val twentyFourHoursMillis = 24 * 60 * 60 * 1000

            // Check if the difference is greater than 24 hours
            timeDifferenceMillis > twentyFourHoursMillis
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
