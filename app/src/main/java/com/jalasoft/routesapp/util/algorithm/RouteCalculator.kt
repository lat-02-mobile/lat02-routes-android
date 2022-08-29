package com.jalasoft.routesapp.util.algorithm

import android.location.Location
import android.location.LocationManager
import com.jalasoft.routesapp.data.model.remote.*

object RouteCalculator {
    fun calculate(cityRoute: CityRouteForAlgorithm, destination: Location, origin: Location, minDistance: Double, minDistanceBtwStops: Double): MutableList<AvailableTransport> {
        val availableTransport: MutableList<AvailableTransport> = mutableListOf()

        val candidates = LinesCandidate()

        for (transport in cityRoute.transportMethods) {
            for (line in transport.lines) {
                val nearestStopToOrigin = line.stops.minWith(Comparator.comparingDouble { it.distanceTo(origin).toDouble() })
                val nearestStopToDestination = line.stops.minWith(Comparator.comparingDouble { it.distanceTo(destination).toDouble() })

                if (nearestStopToDestination.distanceTo(destination).toDouble() <= minDistance && nearestStopToOrigin.distanceTo(origin).toDouble() <= minDistance) {
                    val indexOrigin = line.stops.indexOf(nearestStopToOrigin)
                    val indexDestination = line.stops.indexOf(nearestStopToDestination)

                    if (indexOrigin < indexDestination) {
                        val newStops = line.stops.slice(indexOrigin..indexDestination)
                        val indexOriginPoint = line.routePoints.indexOfFirst {
                            it.latitude == nearestStopToOrigin.latitude && it.longitude == nearestStopToOrigin.longitude
                        }
                        val indexDestinationPoint = line.routePoints.indexOfFirst {
                            it.latitude == nearestStopToDestination.latitude && it.longitude == nearestStopToDestination.longitude
                        }
                        val newLine = line.routePoints.slice(indexOriginPoint..indexDestinationPoint)
                        val route = Route(line.name, newLine, line.start, newStops)
                        availableTransport.add(AvailableTransport(null, mutableListOf(TransportWithLine(transport.name, route))))
                    }
                } else {
                    if (nearestStopToDestination.distanceTo(destination).toDouble() <= minDistance) {
                        val intersectionStopIndex = line.stops.indexOfFirst {
                            it.latitude == nearestStopToDestination.latitude && it.longitude == nearestStopToDestination.longitude
                        }
                        val intersectionRoutePointIndex = line.routePoints.indexOfFirst {
                            it.latitude == nearestStopToDestination.latitude && it.longitude == nearestStopToDestination.longitude
                        }

                        val newStops = line.stops.slice(0..intersectionStopIndex)
                        val newRoutePoints = line.routePoints.slice(0..intersectionRoutePointIndex)
                        val subLine = Route(line.name, newRoutePoints, line.start, newStops)
                        candidates.destinationList.add(TransportWithLine(transport.name, subLine))
                    }

                    if (nearestStopToOrigin.distanceTo(origin).toDouble() <= minDistance) {
                        val intersectionStopIndex = line.stops.indexOfFirst {
                            it.latitude == nearestStopToOrigin.latitude && it.longitude == nearestStopToOrigin.longitude
                        }
                        val intersectionRoutePointIndex = line.routePoints.indexOfFirst {
                            it.latitude == nearestStopToOrigin.latitude && it.longitude == nearestStopToOrigin.longitude
                        }

                        val newStops = line.stops.slice(intersectionStopIndex until line.stops.size)
                        val newRoutePoints = line.routePoints.slice(intersectionRoutePointIndex until line.routePoints.size)
                        val subline = Route(line.name, newRoutePoints, line.start, newStops)

                        candidates.originList.add(TransportWithLine(transport.name, subline))
                    }
                }
            }
        }

        for (routeFromOrigin in candidates.originList) {
            for (routeFromDestiny in candidates.destinationList) {
                for (stop in routeFromDestiny.line.stops) {
                    val nearestStop = routeFromOrigin.line.stops.minWith(Comparator.comparingDouble { it.distanceTo(stop).toDouble() })
                    val indexOfNearestStop = routeFromOrigin.line.stops.indexOf(nearestStop)
                    if (nearestStop.distanceTo(stop).toDouble() <= minDistanceBtwStops) {
                        // Line A
                        val indexOfRoutesA = routeFromOrigin.line.routePoints.indexOfFirst {
                            it.latitude == nearestStop.latitude && it.longitude == nearestStop.longitude
                        }
                        val lineARoute = routeFromOrigin.line.routePoints.slice(0..indexOfRoutesA)
                        val lineAStops = routeFromOrigin.line.stops.slice(0..indexOfNearestStop)
                        val lineA = TransportWithLine(routeFromOrigin.name, Route(routeFromOrigin.line.name, lineARoute, routeFromOrigin.line.start, lineAStops))

                        // Line B
                        val indexOfRoutesB = routeFromDestiny.line.routePoints.indexOfFirst {
                            it.latitude == stop.latitude && it.longitude == stop.longitude
                        }
                        val indexOfStopB = routeFromDestiny.line.stops.indexOfFirst {
                            it.latitude == stop.latitude && it.longitude == stop.longitude
                        }

                        val lineBRoute = routeFromDestiny.line.routePoints.slice(indexOfRoutesB until routeFromDestiny.line.routePoints.size)
                        val lineBStops = routeFromDestiny.line.stops.slice(indexOfStopB until routeFromDestiny.line.stops.size)
                        val lineB = TransportWithLine(routeFromDestiny.name, Route(routeFromDestiny.line.name, lineBRoute, routeFromDestiny.line.start, lineBStops))

                        availableTransport.add(AvailableTransport(indexOfNearestStop, mutableListOf(lineA, lineB)))
                    }
                }
            }
        }
        return availableTransport
    }

    fun coordinatesToLocation(lat: Double, lon: Double): Location {
        val location = Location(LocationManager.NETWORK_PROVIDER)
        location.longitude = lon
        location.latitude = lat
        return location
    }

    fun arrayToMutableListOfLocation(list: List<List<Double>>): MutableList<Location> {
        val points: MutableList<Location> = mutableListOf()
        list.forEach {
            val location = coordinatesToLocation(it[0], it[1])
            points.add(location)
        }
        return points
    }
}
