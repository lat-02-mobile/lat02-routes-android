package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import kotlinx.coroutines.tasks.await
import java.io.Serializable

data class LineRoute(
    val id: String = "",
    val name: String = "",
    val idLine: String = "",
    val line: DocumentReference? = null,
    val start: GeoPoint? = null,
    val end: GeoPoint? = null,
    val routePoints: List<GeoPoint> = listOf(),
    val stops: List<GeoPoint> = listOf()
) : Serializable {
    suspend fun lineRouteToLineRouteLocal(): LineRoutePath {
        val routePoints = GoogleMapsHelper.geoPointListToLocationList(routePoints)
        val start = start?.let { GoogleMapsHelper.geoPointToLocation(it) }
        val end = end?.let { GoogleMapsHelper.geoPointToLocation(it) }
        val stops = GoogleMapsHelper.geoPointListToLocationList(stops)

        var lineRef: DocumentSnapshot?
        var lineName = ""
        line?.let { docRef ->
            lineRef = docRef.get().await()
            lineRef?.let {
                lineName =
                    it.toObject(Line::class.java)?.name ?: ""
            }
        }
        return LineRoutePath(id, name, idLine, lineName, start, end, routePoints, stops)
    }
}

data class LineRoutePath(
    val id: String = "",
    val name: String = "",
    val idLine: String = "",
    val lineName: String? = "",
    val start: Location? = null,
    val end: Location? = null,
    val routePoints: List<Location> = listOf(),
    val stops: List<Location> = listOf()
)
