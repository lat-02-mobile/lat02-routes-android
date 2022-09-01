package com.jalasoft.routesapp.data.model.remote

data class LinesCandidate(
    val originList: MutableList<LinePath> = mutableListOf(),
    val destinationList: MutableList<LinePath> = mutableListOf()
)

data class AvailableTransport(
    val connectionPoint: Int? = null,
    val transports: MutableList<LinePath> = mutableListOf()
)
