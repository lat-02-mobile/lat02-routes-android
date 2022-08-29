package com.jalasoft.routesapp.data.model.remote

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Line(
    val route: List<GeoPoint>,
    val start: List<String>,
    val stop: List<String>
) : Serializable
