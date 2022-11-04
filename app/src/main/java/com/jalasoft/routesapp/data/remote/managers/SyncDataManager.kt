package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.Timestamp
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.local.room.interfaces.RouteLocalRepository
import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.model.local.SyncHistoryEntity
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.data.remote.interfaces.SyncDataRepository
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository
import javax.inject.Inject

class SyncDataManager
@Inject
constructor(private val localDB: LocalDataBaseRepository, private val tourPointsRepository: TourPointRepository, private val tourPointLocalRepository: TourPointLocalRepository, private val routeRepository: RouteRepository, private val routeLocalRepository: RouteLocalRepository) : SyncDataRepository {

    override suspend fun updateLocalLineCategory(cityId: String, localHistory: List<SyncHistoryEntity>) {
        if (localHistory.isNotEmpty()) {
            val history = localHistory.first()
            val resultList = routeRepository.searchForUpdatedLineCategory(history.lineCategoryLastUpdated)
            if (resultList.isNotEmpty()) {
                for (item in resultList) {
                    routeLocalRepository.updateLocalLineCategory(item)
                }
                val currentDate = Timestamp.now()
                val updateHistory = SyncHistoryEntity(history.cityId, currentDate.toDate().time, history.linesLastUpdated, history.lineRoutesLastUpdated, history.TourPointCategoryLastUpdated, history.TourPointLastUpdated)
                localDB.updateSyncHistory(updateHistory)
            }
            updateLocalLines(cityId, history)
        }
    }

    override suspend fun updateLocalLines(cityId: String, history: SyncHistoryEntity) {
        val resultList = routeRepository.searchForUpdatedLines(cityId, history.linesLastUpdated)
        if (resultList.isNotEmpty()) {
            for (item in resultList) {
                routeLocalRepository.updateLocalLines(item)
                updateLocalLineRoutes(item.idLine, history)
            }
            val currentDate = Timestamp.now()
            val updateHistory = SyncHistoryEntity(history.cityId, history.lineCategoryLastUpdated, currentDate.toDate().time, history.lineRoutesLastUpdated, history.TourPointCategoryLastUpdated, history.TourPointLastUpdated)
            localDB.updateSyncHistory(updateHistory)
        } else {
            val localList = routeLocalRepository.getAllLocalLinesByCityId(cityId)
            for (item in localList) {
                updateLocalLineRoutes(item.idLine, history)
            }
        }
        updateTourPoints(cityId, history)
    }

    override suspend fun updateLocalLineRoutes(idLine: String, history: SyncHistoryEntity) {
        val resultList = routeRepository.searchForUpdatedLineRoutes(idLine, history.lineRoutesLastUpdated)
        if (resultList.isNotEmpty()) {
            routeLocalRepository.updateLocalLineRoutes(resultList)
            val currentDate = Timestamp.now()
            val updateHistory = SyncHistoryEntity(history.cityId, history.lineCategoryLastUpdated, history.linesLastUpdated, currentDate.toDate().time, history.TourPointCategoryLastUpdated, history.TourPointLastUpdated)
            localDB.updateSyncHistory(updateHistory)
        }
    }

    override suspend fun updateTourPointsCategory(history: SyncHistoryEntity) {
        val resultList = tourPointsRepository.searchForUpdatedTourPointsCategory(history.TourPointCategoryLastUpdated)
        if (resultList.isNotEmpty()) {
            for (item in resultList) {
                tourPointLocalRepository.updateLocalTourPointCategory(item)
            }
            val currentDate = Timestamp.now()
            val updateHistory = SyncHistoryEntity(history.cityId, history.lineCategoryLastUpdated, history.linesLastUpdated, history.lineRoutesLastUpdated, currentDate.toDate().time, history.TourPointLastUpdated)
            localDB.updateSyncHistory(updateHistory)
        }
    }

    override suspend fun updateTourPoints(cityId: String, history: SyncHistoryEntity) {
        val resultList = tourPointsRepository.searchForUpdatedTourPoints(cityId, history.TourPointLastUpdated)
        if (resultList.isNotEmpty()) {
            for (item in resultList) {
                tourPointLocalRepository.updateLocalTourPoint(item)
            }
            val currentDate = Timestamp.now()
            val updateHistory = SyncHistoryEntity(history.cityId, history.lineCategoryLastUpdated, history.linesLastUpdated, history.lineRoutesLastUpdated, history.TourPointCategoryLastUpdated, currentDate.toDate().time)
            localDB.updateSyncHistory(updateHistory)
        }
        updateTourPointsCategory(history)
    }
}
