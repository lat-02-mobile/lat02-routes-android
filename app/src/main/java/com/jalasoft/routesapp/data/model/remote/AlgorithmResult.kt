package com.jalasoft.routesapp.data.model.remote

import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper

data class LinesCandidate(
    val originList: MutableList<LineRoutePath> = mutableListOf(),
    val destinationList: MutableList<LineRoutePath> = mutableListOf()
)

data class AvailableTransport(
    val connectionPoint: Int? = null,
    val transports: MutableList<LineRoutePath> = mutableListOf()
) {
    private fun calculateTotalDistance(): Int {
        var totalDistance = 0
        for (line in transports) {
            totalDistance += GoogleMapsHelper.getLocationListDistance(line.routePoints).toInt()
        }
        return totalDistance
    }

    fun calculateEstimatedTimeToArrive(): Int {
        var totalMins = 0
        for (line in transports) {
            totalMins += GoogleMapsHelper.getEstimatedTimeToArrive(line.averageVelocity, calculateTotalDistance().toDouble()).toInt()
        }
        return totalMins
    }
}
