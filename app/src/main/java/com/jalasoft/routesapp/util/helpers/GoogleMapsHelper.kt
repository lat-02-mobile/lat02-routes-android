package com.jalasoft.routesapp.util.helpers

import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
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

