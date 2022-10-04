package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.util.helpers.LocationHelper.geoPointListToLocationList
import com.jalasoft.routesapp.util.helpers.LocationHelper.geoPointToLocation
import java.io.Serializable

data class LineRoute(
    val id: String = "",
    val name: String = "",
    val idLine: String = "",
    val line: DocumentReference? = null,
    val start: GeoPoint? = null,
    val end: GeoPoint? = null,
    val routePoints: List<GeoPoint> = listOf(),
    val stops: List<GeoPoint> = listOf(),
    val color: String = "",
    val averageVelocity: String = "0.0"
) : Serializable {
    fun lineRouteToLineRouteInfo(): LineRouteInfo {
        val routePoints = geoPointListToLocationList(routePoints)
        val start = start?.let { geoPointToLocation(it) }
        val end = end?.let { geoPointToLocation(it) }
        val stops = geoPointListToLocationList(stops)

        return LineRouteInfo(id, name, idLine, routePoints, start, end, stops, color, averageVelocity.toDouble())
    }
}

data class LineRouteInfo(
    val id: String = "",
    val name: String = "",
    val idLine: String = "",
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val end: Location? = null,
    val stops: List<Location> = listOf(),
    val color: String = "",
    val averageVelocity: Double = 0.0
) : Serializable
