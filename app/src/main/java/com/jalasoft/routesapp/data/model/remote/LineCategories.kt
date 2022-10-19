package com.jalasoft.routesapp.data.model.remote

import com.google.firebase.Timestamp
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.util.helpers.Constants
import java.io.Serializable

data class LineCategories(
    val id: String = "",
    val nameEng: String = "",
    val nameEsp: String = "",
    val whiteIcon: String = Constants.DEFAULT_CATEGORY_WHITE_ICON,
    val blackIcon: String = Constants.DEFAULT_CATEGORY_BLACK_ICON,
    val createAt: Timestamp = Timestamp.now(),
    val updateAt: Timestamp = Timestamp.now()
) : Serializable {

    fun lineCategoriesToLineCategoriesLocal(): LineCategoriesEntity {
        return LineCategoriesEntity(id, nameEng, nameEsp, whiteIcon, blackIcon, createAt.toDate().time, updateAt.toDate().time)
    }
}
