package com.jalasoft.routesapp.util.helpers

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return sdf.format(Date())
    }
}