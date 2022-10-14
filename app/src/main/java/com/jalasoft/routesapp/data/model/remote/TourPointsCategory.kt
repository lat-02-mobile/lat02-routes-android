package com.jalasoft.routesapp.data.model.remote

import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import java.io.Serializable

data class TourPointsCategory(
    val id: String = "",
    val icon: String = "",
    val descriptionEng: String = "",
    val descriptionEsp: String = ""
) : Serializable {

    fun tourPointCategoryToTourPointCategoryLocal(): TourPointsCategoryEntity {
        return TourPointsCategoryEntity(id, icon, descriptionEng, descriptionEsp)
    }
}
