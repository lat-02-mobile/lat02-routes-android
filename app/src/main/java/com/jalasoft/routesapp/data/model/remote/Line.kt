package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.*

data class Line(
    val id: String? = null,
    val categoryRef: DocumentReference? = null,
    val idCity: String = "",
    val name: String = "",
    val enable: Boolean? = true,
    val routePoints: List<GeoPoint> = listOf(),
    val start: GeoPoint? = null,
    val end: GeoPoint? = null,
    val stops: List<GeoPoint> = listOf()
) : Serializable {

    suspend fun lineToLinePath(): LinePath {
        val routePoints = GoogleMapsHelper.geoPointListToLocationList(routePoints)
        val start = start?.let { GoogleMapsHelper.geoPointToLocation(it) }
        val end = end?.let { GoogleMapsHelper.geoPointToLocation(it) }
        val stops = GoogleMapsHelper.geoPointListToLocationList(stops)

        var category: DocumentSnapshot?
        var categoryName = ""
        categoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) it.toObject(LineCategories::class.java)?.nameEsp ?: ""
                    else it.toObject(LineCategories::class.java)?.nameEng ?: ""
            }
        }
        return LinePath(name, categoryName, routePoints, start, end, stops)
    }

    suspend fun lineToLineLocal(): LineEntity {
        val lineId = id ?: ""
        var cate = ""
        val ena = enable ?: true
        var category: DocumentSnapshot?
        var categoryName = ""
        categoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) it.toObject(LineCategories::class.java)?.nameEsp ?: ""
                    else it.toObject(LineCategories::class.java)?.nameEng ?: ""
            }
            cate = categoryName
        }
        return LineEntity(lineId, name, idCity, cate, ena)
    }
}

data class LinePath(
    val name: String = "",
    val category: String = "",
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val end: Location? = null,
    val stops: List<Location> = listOf()
) : Serializable {
    companion object {
        fun getOneRouteLine(line: LinePath, nearestStopToDestination: Location, nearestStopToOrigin: Location): AvailableTransport? {
            val indexOrigin = line.stops.indexOf(nearestStopToOrigin)
            val indexDestination = line.stops.indexOf(nearestStopToDestination)

            if (indexOrigin < indexDestination) {
                val newStops = line.stops.slice(indexOrigin..indexDestination)

                val indexOriginPoint = getIndexOfFromLocationList(nearestStopToOrigin, line.routePoints)
                val indexDestinationPoint = getIndexOfFromLocationList(nearestStopToDestination, line.routePoints)

                val newRoutePoints = line.routePoints.slice(indexOriginPoint..indexDestinationPoint)
                val newLine = LinePath(line.name, line.category, newRoutePoints, line.start, line.end, newStops)
                return AvailableTransport(null, mutableListOf(newLine))
            }
            return null
        }

        // This functions separates a line's routePoints and stops
        fun getSubLine(line: LinePath, nearestStop: Location, sliceFromStart: Boolean): LinePath {
            val intersectionStopIndex = getIndexOfFromLocationList(nearestStop, line.stops)
            val intersectionRoutePointIndex = getIndexOfFromLocationList(nearestStop, line.routePoints)

            // if the line is needed to be sliced from start then returns a new line from the beginning to the stop point
            // else the new line is from the stop point until the last point (routePoint and stops)
            return if (sliceFromStart) {
                val newStops = line.stops.slice(0..intersectionStopIndex)
                val newRoutePoints = line.routePoints.slice(0..intersectionRoutePointIndex)
                LinePath(line.name, line.category, newRoutePoints, line.start, line.end, newStops)
            } else {
                val newStops = line.stops.slice(intersectionStopIndex until line.stops.size)
                val newRoutePoints = line.routePoints.slice(intersectionRoutePointIndex until line.routePoints.size)
                LinePath(line.name, line.category, newRoutePoints, line.start, line.end, newStops)
            }
        }

        fun getIndexOfFromLocationList(location: Location, locationList: List<Location>): Int {
            return locationList.indexOfFirst {
                it.latitude == location.latitude && it.longitude == location.longitude
            }
        }
    }
}
