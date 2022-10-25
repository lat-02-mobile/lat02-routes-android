package com.jalasoft.routesapp.data.remote.interfaces

import com.jalasoft.routesapp.data.model.local.SyncHistoryEntity

interface SyncDataRepository {
    suspend fun updateLocalLineCategory(cityId: String, localHistory: List<SyncHistoryEntity>)
    suspend fun updateLocalLines(cityId: String, history: SyncHistoryEntity)
    suspend fun updateLocalLineRoutes(idLine: String, history: SyncHistoryEntity)
    suspend fun updateTourPointsCategory(history: SyncHistoryEntity)
    suspend fun updateTourPoints(cityId: String, history: SyncHistoryEntity)
}
