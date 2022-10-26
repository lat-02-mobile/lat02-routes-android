package com.jalasoft.routesapp.util.helpers

import android.content.Context
import android.graphics.*
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import android.graphics.Bitmap.Config.ARGB_8888
import com.google.maps.android.ui.IconGenerator

object GoogleMapsHelper {
    fun drawPolyline(mMap: GoogleMap, list: List<LatLng>, hexColor: String = Constants.DEFAULT_POLYLINE_COLOR): Polyline {
        return mMap.addPolyline(
            PolylineOptions()
                .addAll(list)
                .width(18f)
                .color(Color.parseColor(hexColor))
                .geodesic(true)
        )
    }

    fun drawDotPolyline(mMap: GoogleMap, list: List<LatLng>, hexColor: String = Constants.DEFAULT_POLYLINE_COLOR): Polyline {
        val dot: PatternItem = Dot()
        val gap: PatternItem = Gap(8.toFloat())
        val pattern = listOf(gap, dot)

        return mMap.addPolyline(
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
        if (averageVelocityMetersSec == 0.0) return 1.0
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

    fun getBitmapMarker(context: Context, resourceId: Int, text: String): Bitmap? {
        val resources = context.resources
        val scale: Float = resources.displayMetrics.density
        val iconGen = IconGenerator(context)
        val drawable = ContextCompat.getDrawable(context, resourceId) ?: return null
        iconGen.setBackground(drawable)

        var bitmap = iconGen.makeIcon()
        bitmap = bitmap.copy(ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE // Text color

        paint.textSize = 14 * scale // Text size

        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE) // Text shadow

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        val x = if (text.length == 1) ((bitmap.width / 2) - 10) else (bounds.width() / 2)
        val y = bitmap.height - bounds.height()
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)

        return bitmap
    }
}
