package com.jalasoft.routesapp.data.local.room.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.*

interface LocalDataBaseRepository {
    fun addLocalFavoriteDestination(lat: Double, lng: Double, name: String, context: Context)
    fun getFavoriteDestinationByCityAndUserId(context: Context): List<FavoriteDestinationEntity>
    fun editFavoriteDestination(favDest: FavoriteDestinationEntity)
    fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity)
    fun addSyncHistory(context: Context)
    fun addSyncHistory(syncHistoryEntity: SyncHistoryEntity)
    fun getSyncHistory(context: Context): List<SyncHistoryEntity>
    fun updateSyncHistory(syncHistoryEntity: SyncHistoryEntity)
}
