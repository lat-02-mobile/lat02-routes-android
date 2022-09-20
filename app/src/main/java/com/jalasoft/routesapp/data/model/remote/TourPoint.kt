package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.LocationHelper.geoPointToLocation
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.*

data class TourPoint(
    val idCity: DocumentReference? = null,
    val name: String? = "",
    val address: String? = "",
    val destination: GeoPoint? = null,
    val urlImage: String? = "",
    val tourPointsCategoryRef: DocumentReference? = null
) : Serializable {

    suspend fun tourPointToTourPointPath(): TourPointPath {
        val destination = destination?.let { geoPointToLocation(it) }
        var city: DocumentSnapshot?
        var cityId = ""
        idCity?.let { docRef ->
            city = docRef.get().await()
            city?.let {
                cityId = it.toObject(City::class.java)?.id ?: ""
            }
        }
        var category: DocumentSnapshot?
        var categoryName = ""
        tourPointsCategoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) it.toObject(TourPointsCategory::class.java)?.descriptionEsp ?: ""
                    else it.toObject(TourPointsCategory::class.java)?.descriptionEng ?: ""
            }
        }
        return TourPointPath(cityId, name, address, destination, urlImage, categoryName)
    }

    suspend fun tourPointToTourPointLocal(): TourPointEntity {
        val dest = destination?.let { geoPointToLocation(it) }
        val lat = dest?.latitude ?: 0.0
        val lng = dest?.longitude ?: 0.0
        val destination = com.jalasoft.routesapp.data.model.local.Location(lat, lng)
        val mName = name ?: ""
        val mAddress = address ?: ""
        val mUrlImage = urlImage ?: ""
        var city: DocumentSnapshot?
        var cityId = ""
        idCity?.let { docRef ->
            city = docRef.get().await()
            city?.let {
                cityId = it.toObject(City::class.java)?.id ?: ""
            }
        }
        var category: DocumentSnapshot?
        var categoryName = ""
        tourPointsCategoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) it.toObject(TourPointsCategory::class.java)?.descriptionEsp ?: ""
                    else it.toObject(TourPointsCategory::class.java)?.descriptionEng ?: ""
            }
        }
        return TourPointEntity(cityId, mName, mAddress, destination, mUrlImage, categoryName)
    }
}

data class TourPointPath(
    val idCity: String? = "",
    val name: String? = "",
    val address: String? = "",
    val destination: Location? = null,
    val urlImage: String? = "",
    val category: String? = ""
) : Serializable
