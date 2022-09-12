package com.jalasoft.routesapp.data.api.models.gmaps

import com.google.gson.annotations.SerializedName

data class OverviewPolyline(
    @SerializedName("points")
    var points: String?
)