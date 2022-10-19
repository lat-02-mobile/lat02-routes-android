package com.jalasoft.routesapp.util.helpers

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return sdf.format(Date())
    }

    fun convertDoubleToTime(time: Double): String {
        val date = Date(time.toLong())
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.format(date)
    }

    fun convertDateToDouble(date: String): Double {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return df.parse(date).time.toDouble()
    }

    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
