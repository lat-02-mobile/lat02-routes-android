package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.util.helpers.LocationHelper.geoPointListToLocationList
import com.jalasoft.routesapp.util.helpers.LocationHelper.geoPointToLocation
import java.io.Serializable

data class LineRoute(
    val end: GeoPoint? = null,
    val idLine: String = "",
    val line: DocumentReference? = null,
    val name: String = "",
    val routePoints: List<GeoPoint> = listOf(),
    val start: GeoPoint? = null,
    val stops: List<GeoPoint> = listOf()
) : Serializable {

    fun lineRouteToLineRouteInfo(): LineRouteInfo {
        val routePoints = geoPointListToLocationList(routePoints)
        val start = start?.let { geoPointToLocation(it) }
        val end = end?.let { geoPointToLocation(it) }
        val stops = geoPointListToLocationList(stops)
        return LineRouteInfo(name, idLine, routePoints, start, end, stops)
    }
}

data class LineRouteInfo(
    val name: String = "",
    val idLine: String = "",
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val end: Location? = null,
    val stops: List<Location> = listOf()
) : Serializable
