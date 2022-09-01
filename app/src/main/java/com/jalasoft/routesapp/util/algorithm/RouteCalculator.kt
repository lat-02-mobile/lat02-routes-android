package com.jalasoft.routesapp.util.algorithm

import android.location.Location
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.data.model.remote.LinesCandidate

object RouteCalculator {
    fun calculate(lines: List<LinePath>, destination: Location, origin: Location, minDistance: Double, minDistanceBtwStops: Double): MutableList<AvailableTransport> {
        val availableTransport: MutableList<AvailableTransport> = mutableListOf()

        val candidates = LinesCandidate()

        for (line in lines) {
            val nearestStopToOrigin = line.stops.minWith(Comparator.comparingDouble { it.distanceTo(origin).toDouble() })
            val nearestStopToDestination = line.stops.minWith(Comparator.comparingDouble { it.distanceTo(destination).toDouble() })

            if (nearestStopToDestination.distanceTo(destination).toDouble() <= minDistance && nearestStopToOrigin.distanceTo(origin).toDouble() <= minDistance) {
                val oneRouteLine = LinePath.getOneRouteLine(line, nearestStopToDestination, nearestStopToOrigin)
                if (oneRouteLine != null) availableTransport.add(oneRouteLine)
            } else {
                if (nearestStopToDestination.distanceTo(destination).toDouble() <= minDistance) {
                    val destinationCandidate = LinePath.getSubLine(line, nearestStopToDestination, true)
                    candidates.destinationList.add(destinationCandidate)
                }

                if (nearestStopToOrigin.distanceTo(origin).toDouble() <= minDistance) {
                    val originCandidate = LinePath.getSubLine(line, nearestStopToOrigin, false)
                    candidates.originList.add(originCandidate)
                }
            }
        }

        for (routeFromOrigin in candidates.originList) {
            for (routeFromDestiny in candidates.destinationList) {
                for (stop in routeFromDestiny.stops) {
                    val nearestStop = routeFromOrigin.stops.minWith(Comparator.comparingDouble { it.distanceTo(stop).toDouble() })
                    if (nearestStop.distanceTo(stop).toDouble() <= minDistanceBtwStops) {
                        val indexOfNearestStop = LinePath.getIndexOfFromLocationList(nearestStop, routeFromOrigin.stops)
                        // Line A
                        val lineA = LinePath.getSubLine(routeFromOrigin, nearestStop, false)
                        // Line B
                        val lineB = LinePath.getSubLine(routeFromDestiny, stop, true)
                        availableTransport.add(AvailableTransport(indexOfNearestStop, mutableListOf(lineA, lineB)))
                    }
                }
            }
        }
        return availableTransport
    }
}
