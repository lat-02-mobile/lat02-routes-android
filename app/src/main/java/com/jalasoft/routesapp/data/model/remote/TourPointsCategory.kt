package com.jalasoft.routesapp.data.model.remote

import com.google.firebase.Timestamp
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import java.io.Serializable

data class TourPointsCategory(
    val id: String = "",
    val icon: String = "",
    val descriptionEng: String = "",
    val descriptionEsp: String = "",
    val createAt: Timestamp = Timestamp.now(),
    val updateAt: Timestamp = Timestamp.now()
) : Serializable {

    fun tourPointCategoryToTourPointCategoryLocal(): TourPointsCategoryEntity {
        return TourPointsCategoryEntity(id, icon, descriptionEng, descriptionEsp, createAt.toDate().time, updateAt.toDate().time)
    }
}
