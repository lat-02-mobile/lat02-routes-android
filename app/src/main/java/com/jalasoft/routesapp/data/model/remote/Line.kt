package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import android.location.LocationManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Line(
    val categoryRef: DocumentReference? = null,
    val idCity: String = "",
    val name: String = "",
    val routePoints: List<GeoPoint> = listOf(),
    val start: GeoPoint? = null,
    val stops: List<GeoPoint> = listOf()
) : Serializable {

    fun lineToLinePath(line: Line): LinePath {
        val routePoints = LinePath.geoPointListToLocationList(routePoints)
        val start = start?.let { LinePath.geoPointToLocation(it) }
        val stops = LinePath.geoPointListToLocationList(stops)
        return LinePath(routePoints, start, stops)
    }
}

data class LinePath(
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val stops: List<Location> = listOf()
) {
    companion object {
        fun geoPointToLocation(data: GeoPoint): Location {
            val newLocation = Location(LocationManager.NETWORK_PROVIDER)
            newLocation.latitude = data.latitude
            newLocation.longitude = data.longitude
            return newLocation
        }

        fun geoPointListToLocationList(dataList: List<GeoPoint>): List<Location> {
            return dataList.map { geoPointToLocation(it) }
        }
    }
}
