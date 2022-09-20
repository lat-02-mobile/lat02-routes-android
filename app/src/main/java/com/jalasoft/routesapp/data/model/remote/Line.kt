package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import android.location.LocationManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.*

data class Line(
    val categoryRef: DocumentReference? = null,
    val idCity: String = "",
    val enable: Boolean? = null,
    val lineRef: DocumentReference? = null,
    val idLine: String = "",
    val name: String = ""
) : Serializable {

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

    suspend fun lineToLinePath(): LineInfo {
        var category: DocumentSnapshot?
        var categoryName = ""
        categoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == "spa") it.toObject(LineCategories::class.java)?.nameEsp ?: ""
                    else it.toObject(LineCategories::class.java)?.nameEng ?: ""
            }
        }
        return LineInfo(name, lineRef, categoryName, idLine)
    }
}

data class LineInfo(
    val name: String = "",
    val lineRef: DocumentReference? = null,
    val category: String = "",
    val idLine: String = ""
) : Serializable

data class LineRoutePath(
    val name: String = "",
    val lineRef: DocumentReference? = null,
    val category: String = "",
    val idLine: String = "",
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val end: Location? = null,
    val stops: List<Location> = listOf(),
    val icons: LineCategoryIcons = LineCategoryIcons(),
    val color: String = "#004696",
    val averageVelocity: Double = 1.0
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
                val newLine = LineRoutePath(line.name, line.lineRef, line.category, line.idLine, newRoutePoints, line.start, line.end, newStops, line.icons, line.color, line.averageVelocity)
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
                LineRoutePath(line.name, line.lineRef, line.category, line.idLine, newRoutePoints, line.start, line.end, newStops, line.icons, line.color, line.averageVelocity)
            } else {
                val newStops = line.stops.slice(intersectionStopIndex until line.stops.size)
                val newRoutePoints = line.routePoints.slice(intersectionRoutePointIndex until line.routePoints.size)
                LineRoutePath(line.name, line.lineRef, line.category, line.idLine, newRoutePoints, line.start, line.end, newStops, line.icons, line.color, line.averageVelocity)
            }
        }

        fun getIndexOfFromLocationList(location: Location, locationList: List<Location>): Int {
            return locationList.indexOfFirst {
                it.latitude == location.latitude && it.longitude == location.longitude
            }
        }
    }
}

data class LineRoute(
    val idLine: String = "",
    val lineRef: DocumentReference? = null,
    val name: String = "",
    val start: GeoPoint? = null,
    val end: GeoPoint? = null,
    val routePoints: List<GeoPoint> = listOf(),
    val stops: List<GeoPoint> = listOf()
) : Serializable {
    fun lineRouteToLineRoutePath(): LineRoutePath {
        val routePoints = Line.geoPointListToLocationList(routePoints)
        val start = start?.let { Line.geoPointToLocation(it) }
        val end = end?.let { Line.geoPointToLocation(it) }
        val stops = Line.geoPointListToLocationList(stops)
        return LineRoutePath(name, lineRef, "", idLine, routePoints, start, end, stops)
    }
}
