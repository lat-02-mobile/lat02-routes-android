package com.jalasoft.routesapp.data.local.room.managers

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.model.local.*
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.DateHelper
import java.util.*

class LocalDataBaseManager(private val localRoutesDB: RoutesDB) : LocalDataBaseRepository {
    override fun addLocalFavoriteDestination(lat: Double, lng: Double, name: String, context: Context) {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: ""
        val destination = Location(lat, lng)
        val dateStr = DateHelper.getCurrentDate()
        val date = DateHelper.convertDateToDouble(dateStr)
        val favoriteDestination = FavoriteDestinationEntity(0, name, destination, currentCityId, userId, date)
        localRoutesDB.favoriteDestinationDao().addFavoriteDestination(favoriteDestination)
    }

    override fun getFavoriteDestinationByCityAndUserId(context: Context): List<FavoriteDestinationEntity> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: ""
        return localRoutesDB.favoriteDestinationDao().getFavoriteDestinationsByCityId(currentCityId, userId)
    }

    override fun editFavoriteDestination(favDest: FavoriteDestinationEntity) {
        localRoutesDB.favoriteDestinationDao().editFavoriteDestination(favDest)
    }

    override fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity) {
        localRoutesDB.favoriteDestinationDao().deleteFavoriteDestination(favoriteDestinationEntity)
    }

    override fun addSyncHistory(context: Context) {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val currentDate = Date()
        val history = SyncHistoryEntity(currentCityId, currentDate.time, currentDate.time, currentDate.time, currentDate.time, currentDate.time)
        localRoutesDB.syncHistoryDao().addSyncHistoryDao(history)
    }

    override fun getSyncHistory(context: Context): List<SyncHistoryEntity> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        return localRoutesDB.syncHistoryDao().getAllSyncHisotry(currentCityId)
    }

    override fun updateSyncHistory(syncHistoryEntity: SyncHistoryEntity) {
        localRoutesDB.syncHistoryDao().updateSyncHistoryEntity(syncHistoryEntity)
    }
}
