package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import android.location.LocationManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.*

data class Line(
    val categoryRef: DocumentReference? = null,
    val idCity: String = "",
    val name: String = "",
    val routePoints: List<GeoPoint> = listOf(),
    val start: GeoPoint? = null,
    val end: GeoPoint? = null,
    val stops: List<GeoPoint> = listOf()
) : Serializable {

    suspend fun lineToLinePath(): LinePath {
        val routePoints = LinePath.geoPointListToLocationList(routePoints)
        val start = start?.let { LinePath.geoPointToLocation(it) }
        val end = end?.let { LinePath.geoPointToLocation(it) }
        val stops = LinePath.geoPointListToLocationList(stops)
        var category: DocumentSnapshot?
        var categoryName = ""
        categoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == "spa") it.toObject(LineCategories::class.java)?.nameEsp ?: ""
                    else it.toObject(LineCategories::class.java)?.nameEng ?: ""
            }
        }
        return LinePath(name, categoryName, routePoints, start, end, stops)
    }
}

data class LinePath(
    val name: String = "",
    val category: String = "",
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val end: Location? = null,
    val stops: List<Location> = listOf()
) : Serializable {
    companion object {
        fun geoPointToLocation(data: GeoPoint): Location {
            val newLocation = Location(LocationManager.NETWORK_PROVIDER)
            newLocation.latitude = data.latitude
            newLocation.longitude = data.longitude
            return newLocation
        }

        fun geoPointListToLocationList(dataList: List<GeoPoint>): List<Location> {
            return dataList.map { geoPointToLocation(it) }
        }
    }
}
