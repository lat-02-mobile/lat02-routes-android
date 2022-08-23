package com.jalasoft.routesapp.data.model.remote

data class CityRoute(
    var Bus: List<Line> = listOf(),
    var Subway: List<Line> = listOf(),
    var CountryId: String = "",
    var id: String = "",
    var name: String = ""
)
