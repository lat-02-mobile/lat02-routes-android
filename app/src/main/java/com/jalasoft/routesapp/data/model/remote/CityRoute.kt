package com.jalasoft.routesapp.data.model.remote

import java.io.Serializable

data class CityRoute(
    var Bus: List<Line> = listOf(),
    var Subway: List<Line> = listOf(),
    var CountryId: String = "",
    var id: String = "",
    var lat: String = "",
    var lng: String = "",
    var name: String = ""
) : Serializable
