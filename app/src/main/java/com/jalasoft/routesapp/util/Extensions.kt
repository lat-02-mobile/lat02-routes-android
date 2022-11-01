package com.jalasoft.routesapp.util

import android.location.Location
import android.location.LocationManager
import android.text.Editable
import com.google.android.gms.maps.model.LatLng

object Extensions {
    fun Location.toLatLong(): LatLng {
        return LatLng(latitude, longitude)
    }

    fun LatLng.toLocation(): Location {
        val newLocation = Location(LocationManager.NETWORK_PROVIDER)
        newLocation.latitude = latitude
        newLocation.longitude = longitude
        return newLocation
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
