package com.jalasoft.routesapp.data.model.remote

data class Line(
    val route: List<List<String>>,
    val start: List<String>,
    val stop: List<String>
)
