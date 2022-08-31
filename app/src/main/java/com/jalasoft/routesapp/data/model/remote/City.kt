package com.jalasoft.routesapp.data.model.remote

import java.io.Serializable

data class City(
    val country: String = "",
    val id: String = "",
    val idCountry: String = "",
    val lat: String = "",
    val lng: String = "",
    val name: String = ""
) : Serializable
