package com.jalasoft.routesapp.util.helpers

import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.util.Extensions.toLatLong

object GoogleMapsHelper {
    fun drawPolyline(mMap: GoogleMap, list: List<Location>, hexColor: String = "#004696") {
        val latLongList = list.map { it.toLatLong() }
        mMap.addPolyline(
            PolylineOptions()
                .addAll(latLongList)
                .width(10f)
                .color(Color.parseColor(hexColor))
                .geodesic(true)
        )
    }
}