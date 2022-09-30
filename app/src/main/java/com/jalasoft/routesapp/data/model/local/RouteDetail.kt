package com.jalasoft.routesapp.data.model.local

import android.location.Location
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import com.jalasoft.routesapp.util.helpers.WalkDirection
import kotlin.math.roundToInt

data class RouteDetail(
    val name: String,
    val category: String,
    val whiteIcon: String,
    val blackIcon: String,
    val estimatedTime: Int, // time in minutes
    val estimatedDistance: Int, // distance in meters
    val walkDirection: WalkDirection
) {
    companion object {
        fun getRouteDetailFromLocationList(
            routeName: String,
            list: List<Location>,
            whiteIcon: String,
            blackIcon: String,
            category: String = "",
            averageVelocity: Double = Constants.AVG_WALKING_PACE,
            walkDirection: WalkDirection = WalkDirection.IS_NOT_WALKING
        ): RouteDetail {
            val distance = GoogleMapsHelper.getLocationListDistance(list)
            val estimatedTime = distance / averageVelocity / 60
            return RouteDetail(routeName, category, whiteIcon, blackIcon, estimatedTime.roundToInt(), distance.roundToInt(), walkDirection)
        }
    }
}
