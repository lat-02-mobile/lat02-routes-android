package com.jalasoft.routesapp.data.model.local

data class Location(
    var latitude: Double,
    var longitude: Double
) {
    fun toAndroidLocation(): android.location.Location {
        val newLocation = android.location.Location("")
        newLocation.latitude = latitude
        newLocation.longitude = longitude
        return newLocation
    }
}
