package com.jalasoft.routesapp.data.model.remote

import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import java.io.Serializable

data class LineCategories(
    val id: String = "",
    val nameEng: String = "",
    val nameEsp: String = ""
) : Serializable {

    fun lineCategoriesToLineCategoriesLocal(): LineCategoriesEntity {
        return LineCategoriesEntity(id, nameEng, nameEsp)
    }
}
