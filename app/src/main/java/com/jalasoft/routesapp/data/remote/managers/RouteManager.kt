package com.jalasoft.routesapp.data.remote.managers

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.local.LineRouteAux
import com.jalasoft.routesapp.data.model.remote.*
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.Response
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
        val result = firebaseManager.getAllDocuments<Line>(FirebaseCollections.Lines).data
        if (result != null) {
            result.map {
                val resultCity = firebaseManager.getDocumentsWithCondition<City>(FirebaseCollections.Cities, "id", it.idCity).data
                if (resultCity != null) {
                    val city = resultCity.first()
                    var cate = ""
                    var idCat = ""
                    var category: DocumentSnapshot?
                    var categoryName = ""
                    var catId = ""
                    it.categoryRef?.let { docRef ->
                        category = docRef.get().await()
                        category?.let { ref ->
                            catId = ref.toObject(LineCategories::class.java)?.id ?: ""
                            val currLang = Locale.getDefault().isO3Language
                            categoryName =
                                if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) ref.toObject(LineCategories::class.java)?.nameEsp ?: ""
                                else ref.toObject(LineCategories::class.java)?.nameEng ?: ""
                        }
                        idCat = catId
                        cate = categoryName
                    }
                    val line = LineAux(it.id, it.name, it.enable, idCat, cate, it.idCity, city.name)
                    list.add(line)
                }
            }
            return list
        }
        return listOf()
    }

    override suspend fun getAllLineCategories(): List<LineCategories> {
        val result = firebaseManager.getAllDocuments<LineCategories>(FirebaseCollections.LineCategories).data
        if (result != null) {
            return result
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

    override suspend fun addNewLine(name: String, idCategory: String, idCity: String, enable: Boolean): Response<String> {
        val docId = firebaseManager.getDocId(FirebaseCollections.Lines)
        val date = Date()
        val ref: DocumentReference = firebaseManager.db.document(FirebaseCollections.LineCategories.toString() + "/" + idCategory)
        val line = Line(docId, ref, idCategory, idCity, enable, name, Timestamp(date), Timestamp(date))
        return firebaseManager.addDocument(docId, line, FirebaseCollections.Lines)
    }

    override suspend fun updateLine(idLine: String, name: String, idCategory: String, idCity: String, enable: Boolean): Response<String> {
        val date = Date()
        val ref: DocumentReference = firebaseManager.db.document(FirebaseCollections.LineCategories.toString() + "/" + idCategory)
        val line = Line(idLine, ref, idCategory, idCity, enable, name, Timestamp(date), Timestamp(date))
        return firebaseManager.addDocument(idLine, line, FirebaseCollections.Lines)
    }

    override suspend fun deleteLine(idLine: String): Response<String> {
        return firebaseManager.deleteDocument(idLine, FirebaseCollections.Lines)
    }

    override suspend fun updateLineRoutes(routeId: String, routePoints: List<GeoPoint>, routeStops: List<GeoPoint>): Response<Unit> {
        val updates = hashMapOf(
            "end" to routeStops.last(),
            "start" to routeStops.first(),
            "routePoints" to routePoints,
            "stops" to routeStops,
            "updateAt" to FieldValue.serverTimestamp()
        )
        return firebaseManager.updateDocument(routeId, FirebaseCollections.LineRoute, updates)
    }

    override fun getAllRoutesForLine(idLine: String, completion: (Response<List<LineRouteInfo>>) -> Unit) {
        firebaseManager.listenDocumentsWithQuery<LineRoute>(FirebaseCollections.LineRoute, "idLine", idLine) { result ->
            when (result) {
                is Response.Success -> {
                    val data = result.data
                    if (data != null) {
                        val lineRoutes = data.map {
                            it.lineRouteToLineRouteInfo()
                        }
                        completion(Response.Success(lineRoutes))
                        return@listenDocumentsWithQuery
                    }
                    completion(Response.Success(listOf()))
                }
                is Response.Error -> {
                    completion(Response.Error(result.message.toString()))
                }
            }
        }
    }

    override suspend fun updateRouteInfo(lineRouteInfo: LineRouteAux): Response<Unit> {
        val updates = hashMapOf(
            "averageVelocity" to lineRouteInfo.velocity.toString(),
            "color" to lineRouteInfo.color,
            "name" to lineRouteInfo.name,
            "updateAt" to FieldValue.serverTimestamp()
        )
        return firebaseManager.updateDocument(lineRouteInfo.routeId, FirebaseCollections.LineRoute, updates)
    }

    override suspend fun createRouteForLine(lineRouteInfo: LineRouteAux): Response<Unit> {
        val idRoute = firebaseManager.getDocId(FirebaseCollections.LineRoute)
        val lineRef = firebaseManager.db.collection(FirebaseCollections.LineRoute.toString()).document(lineRouteInfo.lineId)
        val createRoute = hashMapOf(
            "averageVelocity" to lineRouteInfo.velocity.toString(),
            "color" to lineRouteInfo.color,
            "createAt" to FieldValue.serverTimestamp(),
            "end" to GeoPoint(0.0, 0.0),
            "id" to idRoute,
            "idLine" to lineRouteInfo.lineId,
            "line" to lineRef,
            "name" to lineRouteInfo.name,
            "routePoints" to listOf<GeoPoint>(),
            "start" to GeoPoint(0.0, 0.0),
            "stops" to listOf<GeoPoint>(),
            "updateAt" to FieldValue.serverTimestamp()
        )
        return firebaseManager.addDocument(idRoute, createRoute, FirebaseCollections.LineRoute)
    }

    override suspend fun deleteRouteInLine(routeId: String): Response<Unit> {
        return Response.Success(Unit)
    }
}
