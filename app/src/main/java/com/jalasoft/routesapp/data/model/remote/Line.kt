package com.jalasoft.routesapp.data.model.remote

import android.location.Location
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.util.helpers.Constants
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.*

data class Line(
    val id: String = "",
    val categoryRef: DocumentReference? = null,
    val idCity: String = "",
    val enable: Boolean? = null,
    val name: String = ""
) : Serializable {
    suspend fun lineToLineInfo(routePaths: List<LineRouteInfo>): LineInfo {
        var category: DocumentSnapshot?
        var categoryName = ""
        categoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == "spa") it.toObject(LineCategories::class.java)?.nameEsp ?: ""
                    else it.toObject(LineCategories::class.java)?.nameEng ?: ""
            }
        }
        return LineInfo(id, name, enable, categoryName, routePaths)
    }

    suspend fun lineToLineLocal(): LineEntity {
        var cate = ""
        val ena = enable ?: true
        var category: DocumentSnapshot?
        var categoryName = ""
        categoryRef?.let { docRef ->
            category = docRef.get().await()
            category?.let {
                val currLang = Locale.getDefault().isO3Language
                categoryName =
                    if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) it.toObject(LineCategories::class.java)?.nameEsp ?: ""
                    else it.toObject(LineCategories::class.java)?.nameEng ?: ""
            }
            cate = categoryName
        }
        return LineEntity(id, name, idCity, cate, ena)
    }
}

data class LineInfo(
    val id: String = "",
    val name: String = "",
    val enable: Boolean? = null,
    val category: String = "",
    val routePaths: List<LineRouteInfo> = listOf()
) {
    fun toLineRoutePaths(): List<LineRoutePath> {
        val lineRoutePathList = mutableListOf<LineRoutePath>()
        routePaths.map { route ->
            lineRoutePathList.add(LineRoutePath(id, name, category, route.name, route.routePoints, route.start, route.end, route.stops))
        }
        return lineRoutePathList.toList()
    }
}

// class to be used in algorithm
data class LineRoutePath(
    val idLine: String = "",
    val lineName: String = "",
    val category: String = "",
    val routeName: String = "",
    val routePoints: List<Location> = listOf(),
    val start: Location? = null,
    val end: Location? = null,
    val stops: List<Location> = listOf(),
    val icons: LineCategoryIcons = LineCategoryIcons(),
    val color: String = "#004696",
    val averageVelocity: Double = 1.0
) : Serializable
