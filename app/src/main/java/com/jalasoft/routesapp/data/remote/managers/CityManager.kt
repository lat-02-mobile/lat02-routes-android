package com.jalasoft.routesapp.data.remote.managers

import android.content.Context
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.TourPoint
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

class CityManager(private val firebaseManager: FirebaseManager) : CityRepository {

    override suspend fun getAllCities(): List<City> {
        val result = firebaseManager.getAllDocuments<City>(FirebaseCollections.Cities).data
        return result ?: listOf()
    }

    override suspend fun getAllTourPoints(context: Context): List<TourPointEntity> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val cityRef = firebaseManager.db.document("${FirebaseCollections.Cities}/$currentCityId")

        val result = firebaseManager.getDocumentsByReferenceWithCondition<TourPoint>(FirebaseCollections.Tourpoints, "idCity", cityRef).data
        if (result != null) {
            val tourPointLocalList = result.map {
                it.tourPointToTourPointLocal()
            }
            return tourPointLocalList
        }
        return listOf()
    }
}
