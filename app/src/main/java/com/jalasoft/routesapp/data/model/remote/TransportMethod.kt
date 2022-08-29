package com.jalasoft.routesapp.data.model.remote

import android.location.Location

data class TransportMethod(
    val name: String,
    val lines: List<Route>
)

data class Route(
    val name: String,
    val routePoints: List<Location>,
    val start: Location,
    val stops: List<Location>
)
