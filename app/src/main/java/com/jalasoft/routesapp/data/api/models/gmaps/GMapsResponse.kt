package com.jalasoft.routesapp.data.api.models.gmaps

data class GMapsResponse(
    val html_attributions: List<Any>,
    val results: List<Place>,
    val status: String
)
