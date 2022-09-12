package com.jalasoft.routesapp.data.api.models.gmaps

import com.google.gson.annotations.SerializedName

data class Polyline(
    @SerializedName("points")
    var points: String?
)