package com.jalasoft.routesapp.data.model.remote

import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper

data class LinesCandidate(
    val originList: MutableList<LinePath> = mutableListOf(),
    val destinationList: MutableList<LinePath> = mutableListOf()
)

data class AvailableTransport(
    val connectionPoint: Int? = null,
    val transports: MutableList<LinePath> = mutableListOf()
) {
    fun calculateTotalDistance(): Int {
        var totalDistance = 0
        for ( line in transports) {
            totalDistance += GoogleMapsHelper.getLocationListDistance(line.routePoints).toInt()
        }
        return totalDistance
    }
}
