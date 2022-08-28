package com.jalasoft.routesapp.util

import android.content.Context
import android.util.TypedValue

object ConverterFromPixelToDpUtil {
    fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}
