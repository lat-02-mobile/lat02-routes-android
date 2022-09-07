package com.jalasoft.routesapp.util

import android.location.Location
import com.google.android.gms.maps.model.LatLng

object Extensions {
    fun Location.toLatLong(): LatLng {
        return LatLng(latitude, longitude)
    }
}