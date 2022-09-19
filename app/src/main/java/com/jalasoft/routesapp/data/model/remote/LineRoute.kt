package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import kotlinx.coroutines.tasks.await
import java.io.Serializable

data class LineRoute(
    val id: String = "",
    val name: String = "",
    val idLine: String = "",
    val line: DocumentReference? = null,
    val start: GeoPoint? = null,
    val end: GeoPoint? = null,
    val routePoints: List<GeoPoint> = listOf(),
    val stops: List<GeoPoint> = listOf()
) : Serializable {
    suspend fun lineRouteToLineRoutePath(): LineRoutePath {
        val routePoints = GoogleMapsHelper.geoPointListToLocationList(routePoints)
        val start = start?.let { GoogleMapsHelper.geoPointToLocation(it) }
        val end = end?.let { GoogleMapsHelper.geoPointToLocation(it) }
        val stops = GoogleMapsHelper.geoPointListToLocationList(stops)

        var lineRef: DocumentSnapshot?
        var lineName = ""
        line?.let { docRef ->
            lineRef = docRef.get().await()
            lineRef?.let {
                lineName =
                    it.toObject(Line::class.java)?.name ?: ""
            }
        }
        return LineRoutePath(id, name, line, idLine, routePoints, start, end, stops)
    }
}

data class LineRoutePath(
    val id: String = "",
    val name: String = "",
    val lineRef: DocumentReference? = null,
    val idLine: String = "",
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val end: Location? = null,
    val stops: List<Location> = listOf()
) : Serializable {
    companion object {
        fun getOneRouteLine(line: LineRoutePath, nearestStopToDestination: Location, nearestStopToOrigin: Location): AvailableTransport? {
            val indexOrigin = line.stops.indexOf(nearestStopToOrigin)
            val indexDestination = line.stops.indexOf(nearestStopToDestination)

            if (indexOrigin < indexDestination) {
                val newStops = line.stops.slice(indexOrigin..indexDestination)

                val indexOriginPoint = getIndexOfFromLocationList(nearestStopToOrigin, line.routePoints)
                val indexDestinationPoint = getIndexOfFromLocationList(nearestStopToDestination, line.routePoints)

                val newRoutePoints = line.routePoints.slice(indexOriginPoint..indexDestinationPoint)
                val newLine = LineRoutePath(line.id, line.name, line.lineRef, line.idLine, newRoutePoints, line.start, line.end, newStops)
                return AvailableTransport(null, mutableListOf(newLine))
            }
            return null
        }

        // This functions separates a line's routePoints and stops
        fun getSubLine(line: LineRoutePath, nearestStop: Location, sliceFromStart: Boolean): LineRoutePath {
            val intersectionStopIndex = getIndexOfFromLocationList(nearestStop, line.stops)
            val intersectionRoutePointIndex = getIndexOfFromLocationList(nearestStop, line.routePoints)

            // if the line is needed to be sliced from start then returns a new line from the beginning to the stop point
            // else the new line is from the stop point until the last point (routePoint and stops)
            return if (sliceFromStart) {
                val newStops = line.stops.slice(0..intersectionStopIndex)
                val newRoutePoints = line.routePoints.slice(0..intersectionRoutePointIndex)
                LineRoutePath(line.id, line.name, line.lineRef, line.idLine, newRoutePoints, line.start, line.end, newStops)
            } else {
                val newStops = line.stops.slice(intersectionStopIndex until line.stops.size)
                val newRoutePoints = line.routePoints.slice(intersectionRoutePointIndex until line.routePoints.size)
                LineRoutePath(line.id, line.name, line.lineRef, line.idLine, newRoutePoints, line.start, line.end, newStops)
            }
        }

        fun getIndexOfFromLocationList(location: Location, locationList: List<Location>): Int {
            return locationList.indexOfFirst {
                it.latitude == location.latitude && it.longitude == location.longitude
            }
        }
    }
}
