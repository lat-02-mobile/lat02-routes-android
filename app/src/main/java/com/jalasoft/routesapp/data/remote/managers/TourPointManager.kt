package com.jalasoft.routesapp.data.remote.managers

import android.content.Context
import com.jalasoft.routesapp.data.model.remote.TourPoint
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

class TourPointManager(private val firebaseManager: FirebaseManager) : TourPointRepository {
    override suspend fun getAllTourPoints(context: Context): List<TourPointPath> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val cityRef = firebaseManager.db.document("${FirebaseCollections.Cities}/$currentCityId")

        val result = firebaseManager.getDocumentsByReferenceWithCondition<TourPoint>(FirebaseCollections.Tourpoints, "idCity", cityRef).data
        if (result != null) {
            val tourPointPathList = result.map {
                it.tourPointToTourPointPath()
            }
            return tourPointPathList
        }
        return listOf()
    }
}
