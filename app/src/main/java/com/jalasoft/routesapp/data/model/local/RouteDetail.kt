package com.jalasoft.routesapp.data.model.local

import com.jalasoft.routesapp.data.model.remote.LineCategoryIcons
import com.jalasoft.routesapp.util.helpers.WalkDirection

data class RouteDetail(
    val name: String,
    val category: String,
    val icon: LineCategoryIcons,
    val estimatedTime: Int, // time in minutes
    val estimatedDistance: Int, // distance in meters
    val walkDirection: WalkDirection
)
