package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import android.location.LocationManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.util.helpers.Constants
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.*

data class Line(
    val id: String? = null,
    val categoryRef: DocumentReference? = null,
    val idCity: String = "",
    val enable: Boolean? = null,
    val lineRef: DocumentReference? = null,
    val idLine: String = "",
    val name: String = ""
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

    suspend fun lineToLinePath(): LineInfo {
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
        return LineInfo(name, lineRef, categoryName, idLine)
    }

    suspend fun lineToLineLocal(): LineEntity {
        val lineId = id ?: ""
        var cate = ""
        val ena = enable ?: true
        var category: DocumentSnapshot?
        var categoryName = ""
        categoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) it.toObject(LineCategories::class.java)?.nameEsp ?: ""
                    else it.toObject(LineCategories::class.java)?.nameEng ?: ""
            }
            cate = categoryName
        }
        return LineEntity(lineId, name, idCity, cate, ena)
    }
}

data class LineInfo(
    val name: String = "",
    val lineRef: DocumentReference? = null,
    val category: String = "",
    val idLine: String = ""
) : Serializable
