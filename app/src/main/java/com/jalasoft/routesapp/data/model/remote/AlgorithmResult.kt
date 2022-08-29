package com.jalasoft.routesapp.data.model.remote

data class LinesCandidate(
    val originList: MutableList<TransportWithLine> = mutableListOf(),
    val destinationList: MutableList<TransportWithLine> = mutableListOf()
)

data class TransportWithLine(
    val name: String,
    val line: Route
)

data class AvailableTransport(
    val connectionPoint: Int? = null,
    val transports: MutableList<TransportWithLine> = mutableListOf()
)
