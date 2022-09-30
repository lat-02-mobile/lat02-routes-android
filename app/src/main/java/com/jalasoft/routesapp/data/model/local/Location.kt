package com.jalasoft.routesapp.data.model.local

import android.location.LocationManager

data class Location(
    var latitude: Double,
    var longitude: Double
) {
    fun toAndroidLocation(): android.location.Location {
        val newLocation = android.location.Location(LocationManager.NETWORK_PROVIDER)
        newLocation.latitude = latitude
        newLocation.longitude = longitude
        return newLocation
    }
}
