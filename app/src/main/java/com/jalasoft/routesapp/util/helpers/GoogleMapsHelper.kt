package com.jalasoft.routesapp.util.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.jalasoft.routesapp.util.Extensions.toLatLong

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.jalasoft.routesapp.BuildConfig
import com.jalasoft.routesapp.R
import org.json.JSONObject

object GoogleMapsHelper {
    fun drawPolyline(mMap: GoogleMap, list: List<Location>, hexColor: String = "#004696") {
        val latLongList = list.map { it.toLatLong() }
        mMap.addPolyline(
            PolylineOptions()
                .addAll(latLongList)
                .width(18f)
                .color(Color.parseColor(hexColor))
                .geodesic(true)
        )
    }

    fun connectStops(mMap: GoogleMap, context: Context, firstLatLng: LatLng, secondLatLng: LatLng) {
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                "${firstLatLng.latitude},${firstLatLng.longitude}&destination=${secondLatLng.latitude}," +
                "${secondLatLng.longitude}&key=${BuildConfig.GOOGLE_DIRECTIONS_API_KEY}"
        val directionsRequest = object : StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                val dot: PatternItem = Dot()
                val gap: PatternItem = Gap(8.toFloat())

                val pattern = listOf(gap, dot)

                mMap.addPolyline(PolylineOptions().addAll(path[i]).color(R.color.color_primary).pattern(pattern))
            }
        }, Response.ErrorListener {
                _ ->
        }){}
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(directionsRequest)
    }

    // Distance in meters
    fun getLocationListDistance(locations: List<Location>): Double {
        var sum = 0.0
        for (i in locations.indices) {
            if ( i + 1 < locations.size) sum += locations[i].distanceTo(locations[i + 1])
        }
        return sum
    }

    // Estimated time in minutes
    fun getEstimatedTimeToArrive(averageVelocityMetersSec: Double, totalDistanceMeters: Double): Double {
        return (totalDistanceMeters * (1/averageVelocityMetersSec)) / 60
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

