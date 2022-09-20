package com.jalasoft.routesapp.util.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

object GoogleMapsHelper {
    fun drawPolyline(mMap: GoogleMap, list: List<LatLng>, hexColor: String = "#004696") {
        mMap.addPolyline(
            PolylineOptions()
                .addAll(list)
                .width(18f)
                .color(Color.parseColor(hexColor))
                .geodesic(true)
        )
    }

    fun drawDotPolyline(mMap: GoogleMap, list: List<LatLng>, hexColor: String = "#004696") {
        val dot: PatternItem = Dot()
        val gap: PatternItem = Gap(8.toFloat())
        val pattern = listOf(gap, dot)

        mMap.addPolyline(
            PolylineOptions()
                .addAll(list)
                .width(18f)
                .color(Color.parseColor(hexColor))
                .geodesic(true)
                .pattern(pattern)
        )
    }

    // Distance in meters
    fun getLocationListDistance(locations: List<Location>): Double {
        var sum = 0.0
        for (i in locations.indices) {
            if (i + 1 < locations.size) sum += locations[i].distanceTo(locations[i + 1])
        }
        return sum
    }

    // Estimated time in minutes
    fun getEstimatedTimeToArrive(averageVelocityMetersSec: Double, totalDistanceMeters: Double): Double {
        return (totalDistanceMeters * (1 / averageVelocityMetersSec)) / 60
    }

    fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun locationToLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }
}
