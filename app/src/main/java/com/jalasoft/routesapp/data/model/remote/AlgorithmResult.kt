package com.jalasoft.routesapp.data.model.remote

data class LinesCandidate(
    val originList: MutableList<LineRoutePath> = mutableListOf(),
    val destinationList: MutableList<LineRoutePath> = mutableListOf()
)

data class AvailableTransport(
    val connectionPoint: Int? = null,
    val transports: MutableList<LineRoutePath> = mutableListOf()
)
