package com.jalasoft.routesapp.data.source

import android.content.Context
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.model.local.*

class FakeLocalDataBaseManager : LocalDataBaseRepository {
    override fun addLocalFavoriteDestination(lat: Double, lng: Double, name: String, context: Context) {
    }

    override fun getFavoriteDestinationByCityAndUserId(context: Context): List<FavoriteDestinationEntity> {
        return listOf()
    }

    override fun editFavoriteDestination(favDest: FavoriteDestinationEntity) {
    }

    override fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity) {
    }

    override fun addSyncHistory(context: Context) {
    }

    override fun getSyncHistory(context: Context): List<SyncHistoryEntity> {
        return listOf()
    }

    override fun updateSyncHistory(syncHistoryEntity: SyncHistoryEntity) {
    }
}
