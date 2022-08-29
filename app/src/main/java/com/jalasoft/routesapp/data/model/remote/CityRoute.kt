package com.jalasoft.routesapp.data.model.remote

data class CityRoute(
    var Bus: List<Line> = listOf(),
    var Subway: List<Line> = listOf(),
    var CountryId: String = "",
    var id: String = "",
    var lat: String = "",
    var lng: String = "",
    var name: String = ""
)

data class CityRouteForAlgorithm(
    val transportMethods: List<TransportMethod>,
    var CountryId: String = "",
    var id: String = "",
    var lat: String = "",
    var lng: String = "",
    var name: String = ""
)
