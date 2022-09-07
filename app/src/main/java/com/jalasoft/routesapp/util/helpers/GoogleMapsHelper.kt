package com.jalasoft.routesapp.util.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

object GoogleMapsHelper {
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

    fun geoPointToLocation(data: GeoPoint): Location {
        val newLocation = Location(LocationManager.NETWORK_PROVIDER)
        newLocation.latitude = data.latitude
        newLocation.longitude = data.longitude
        return newLocation
    }

    fun coordinatesToLocation(lat: Double, lon: Double): Location {
        val location = Location(LocationManager.NETWORK_PROVIDER)
        location.longitude = lon
        location.latitude = lat
        return location
    }

    fun geoPointListToLocationList(dataList: List<GeoPoint>): List<Location> {
        return dataList.map { geoPointToLocation(it) }
    }
}
