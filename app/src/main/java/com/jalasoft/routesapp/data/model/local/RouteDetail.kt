package com.jalasoft.routesapp.data.model.local

import android.location.Location
import com.jalasoft.routesapp.data.model.remote.LineCategoryIcons
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import com.jalasoft.routesapp.util.helpers.WalkDirection
import kotlin.math.roundToInt

data class RouteDetail(
    val name: String,
    val category: String,
    val icon: LineCategoryIcons,
    val estimatedTime: Int, // time in minutes
    val estimatedDistance: Int, // distance in meters
    val walkDirection: WalkDirection
) {
    companion object {
        fun getRouteDetailFromLocationList(
            routeName: String,
            list: List<Location>,
            icon: LineCategoryIcons = LineCategoryIcons(),
            category: String = "",
            averageVelocity: Double = Constants.AVG_WALKING_PACE,
            walkDirection: WalkDirection = WalkDirection.IS_NOT_WALKING
        ): RouteDetail {
            val distance = GoogleMapsHelper.getLocationListDistance(list)
            val estimatedTime = distance / averageVelocity
            return RouteDetail(routeName, category, icon, estimatedTime.roundToInt(), distance.roundToInt(), walkDirection)
        }
    }
}
