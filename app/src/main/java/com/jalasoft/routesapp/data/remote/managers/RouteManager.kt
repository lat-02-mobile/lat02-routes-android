package com.jalasoft.routesapp.data.remote.managers

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.Timestamp
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.*
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.DateHelper
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import kotlinx.coroutines.tasks.await
import java.util.*

class RouteManager(private val firebaseManager: FirebaseManager) : RouteRepository {

    override suspend fun getAllLinesToSaveLocally(context: Context): List<LineEntity> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val result = firebaseManager.getDocumentsWithCondition<Line>(FirebaseCollections.Lines, "idCity", currentCityId).data
        if (result != null) {
            val lineLocal = result.map {
                it.lineToLineLocal()
            }
            return lineLocal
        }
        return listOf()
    }

    override suspend fun getAllLinesCategoryToSaveLocally(): List<LineCategoriesEntity> {
        val result = firebaseManager.getAllDocuments<LineCategories>(FirebaseCollections.LineCategories).data
        if (result != null) {
            val lineCategories = result.map {
                it.lineCategoriesToLineCategoriesLocal()
            }
            return lineCategories
        }
        return listOf()
    }

    override suspend fun getAllLinesRouteToSaveLocally(idLine: String): List<LineRouteInfo> {
        val result = firebaseManager.getDocumentsWithCondition<LineRoute>(FirebaseCollections.LineRoute, "idLine", idLine).data
        if (result != null) {
            return result.map {
                it.lineRouteToLineRouteInfo()
            }
        }
        return listOf()
    }

    override suspend fun getAllLines(): List<LineAux> {
        val list: MutableList<LineAux> = mutableListOf()
        val result = firebaseManager.getAllDocuments<Line>(FirebaseCollections.LineRoute).data
        if (result != null) {
            result.map {
                val resultCity = firebaseManager.getDocumentsWithCondition<City>(FirebaseCollections.Cities, "id", it.idCity).data
                if (resultCity != null) {
                    val city = resultCity.first()
                    var cate = ""
                    var category: DocumentSnapshot?
                    var categoryName = ""
                    it.categoryRef?.let { docRef ->
                        category = docRef.get().await()
                        category?.let { ref ->
                            val currLang = Locale.getDefault().isO3Language
                            categoryName =
                                if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) ref.toObject(LineCategories::class.java)?.nameEsp ?: ""
                                else ref.toObject(LineCategories::class.java)?.nameEng ?: ""
                        }
                        cate = categoryName
                    }
                    val line = LineAux(it.id, it.name, it.enable, cate, it.idCity, city.name)
                    list.add(line)
                }
            }
            return list
        }
        return listOf()
    }

    override suspend fun searchForUpdatedLineCategory(lineCategoryLastUpdated: Long): List<LineCategoriesEntity> {
        val date = DateHelper.fromTimestamp(lineCategoryLastUpdated)
        val lastUpdated = Timestamp(date)
        val result = firebaseManager.getDocumentsWithConditionByDate<LineCategories>(FirebaseCollections.LineCategories, "updateAt", lastUpdated).data
        if (result != null) {
            val lineCategories = result.map {
                it.lineCategoriesToLineCategoriesLocal()
            }
            return lineCategories
        }
        return listOf()
    }

    override suspend fun searchForUpdatedLines(cityId: String, linesLastUpdated: Long): List<LineEntity> {
        val date = DateHelper.fromTimestamp(linesLastUpdated)
        val lastUpdated = Timestamp(date)
        val result = firebaseManager.getDocumentsWithConditionAndByDate<Line>(FirebaseCollections.Lines, "idCity", cityId, "updateAt", lastUpdated).data
        if (result != null) {
            val lineLocal = result.map {
                it.lineToLineLocal()
            }
            return lineLocal
        }
        return listOf()
    }

    override suspend fun searchForUpdatedLineRoutes(idLine: String, lineRoutesLastUpdated: Long): List<LineRouteInfo> {
        val date = DateHelper.fromTimestamp(lineRoutesLastUpdated)
        val lastUpdated = Timestamp(date)
        val result = firebaseManager.getDocumentsWithConditionAndByDate<LineRoute>(FirebaseCollections.LineRoute, "idLine", idLine, "updateAt", lastUpdated).data
        if (result != null) {
            return result.map {
                it.lineRouteToLineRouteInfo()
            }
        }
        return listOf()
    }
}
