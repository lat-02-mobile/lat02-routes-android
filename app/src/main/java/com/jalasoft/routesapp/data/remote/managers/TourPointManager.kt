package com.jalasoft.routesapp.data.remote.managers

import android.content.Context
import com.google.firebase.Timestamp
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.remote.TourPoint
import com.jalasoft.routesapp.data.model.remote.TourPointsCategory
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.DateHelper
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

class TourPointManager(private val firebaseManager: FirebaseManager) : TourPointRepository {
    override suspend fun getAllTourPointsToSaveLocally(context: Context): List<TourPointEntity> {
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

    override suspend fun getAllTourPointsCategoriesToSaveLocally(): List<TourPointsCategoryEntity> {
        val result = firebaseManager.getAllDocuments<TourPointsCategory>(FirebaseCollections.TourpointsCategory).data
        if (result != null) {
            val tourPointsCategory = result.map {
                it.tourPointCategoryToTourPointCategoryLocal()
            }
            return tourPointsCategory
        }
        return listOf()
    }

    override suspend fun searchForUpdatedTourPoints(context: Context, tourPointLastUpdated: Long): List<TourPointEntity> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val cityRef = firebaseManager.db.document("${FirebaseCollections.Cities}/$currentCityId")
        val date = DateHelper.fromTimestamp(tourPointLastUpdated)
        val lastUpdated = Timestamp(date)
        val result = firebaseManager.getDocumentsByReferenceAndDate<TourPoint>(FirebaseCollections.Tourpoints, "idCity", cityRef, "updateAt", lastUpdated).data
        if (result != null) {
            val tourPoints = result.map {
                it.tourPointToTourPointLocal()
            }
            return tourPoints
        }
        return listOf()
    }

    override suspend fun searchForUpdatedTourPointsCategory(tourPointCategoryLastUpdated: Long): List<TourPointsCategoryEntity> {
        val date = DateHelper.fromTimestamp(tourPointCategoryLastUpdated)
        val lastUpdated = Timestamp(date)
        val result = firebaseManager.getDocumentsWithConditionByDate<TourPointsCategory>(FirebaseCollections.TourpointsCategory, "updateAt", lastUpdated).data
        if (result != null) {
            val tourPointsCategory = result.map {
                it.tourPointCategoryToTourPointCategoryLocal()
            }
            return tourPointsCategory
        }
        return listOf()
    }
}
