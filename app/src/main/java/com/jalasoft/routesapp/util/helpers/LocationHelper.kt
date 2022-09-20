package com.jalasoft.routesapp.util.helpers

import android.location.Location
import android.location.LocationManager
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.data.model.remote.LineRoutePath

object LocationHelper {
    fun geoPointToLocation(data: GeoPoint): Location {
        val newLocation = Location(LocationManager.NETWORK_PROVIDER)
        newLocation.latitude = data.latitude
        newLocation.longitude = data.longitude
        return newLocation
    }

    fun geoPointListToLocationList(dataList: List<GeoPoint>): List<Location> {
        return dataList.map { geoPointToLocation(it) }
    }

    fun getOneRouteLine(line: LineRoutePath, nearestStopToDestination: Location, nearestStopToOrigin: Location): AvailableTransport? {
        val indexOrigin = line.stops.indexOf(nearestStopToOrigin)
        val indexDestination = line.stops.indexOf(nearestStopToDestination)

        if (indexOrigin < indexDestination) {
            val newStops = line.stops.slice(indexOrigin..indexDestination)

            val indexOriginPoint = getIndexOfFromLocationList(nearestStopToOrigin, line.routePoints)
            val indexDestinationPoint = getIndexOfFromLocationList(nearestStopToDestination, line.routePoints)

            val newRoutePoints = line.routePoints.slice(indexOriginPoint..indexDestinationPoint)
            val newLine = LineRoutePath(line.idLine, line.lineName, line.category, line.routeName, newRoutePoints, line.start, line.end, newStops)
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
            LineRoutePath(line.idLine, line.lineName, line.category, line.routeName, newRoutePoints, line.start, line.end, newStops)
        } else {
            val newStops = line.stops.slice(intersectionStopIndex until line.stops.size)
            val newRoutePoints = line.routePoints.slice(intersectionRoutePointIndex until line.routePoints.size)
            LineRoutePath(line.idLine, line.lineName, line.category, line.routeName, newRoutePoints, line.start, line.end, newStops)
        }
    }

    fun getIndexOfFromLocationList(location: Location, locationList: List<Location>): Int {
        return locationList.indexOfFirst {
            it.latitude == location.latitude && it.longitude == location.longitude
        }
    }
}
