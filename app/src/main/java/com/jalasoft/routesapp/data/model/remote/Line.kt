package com.jalasoft.routesapp.data.model.remote

import com.google.firebase.firestore.GeoPoint

data class Line(
    val route: List<GeoPoint>,
    val start: List<String>,
    val stop: List<String>
)
