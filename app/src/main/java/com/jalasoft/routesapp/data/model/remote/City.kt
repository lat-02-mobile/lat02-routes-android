package com.jalasoft.routesapp.data.model.remote

import java.io.Serializable

data class City(
    val name: String = "",
    val country: String = "",
    val countryCode: String = "",
    val countryPhoneCode: String = "",
    val lat: String = "",
    val lng: String = ""
) : Serializable
