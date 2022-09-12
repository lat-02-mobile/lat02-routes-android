package com.jalasoft.routesapp.data.api.models.gmaps

import com.google.gson.annotations.SerializedName

data class DirectionResponse(
    @SerializedName("geocoded_waypoints")
    var geocodedWaypoints: List<GeocodedWaypoint>,
    @SerializedName("routes")
    var routes: List<Route>,
    @SerializedName("status")
    var status: String
)