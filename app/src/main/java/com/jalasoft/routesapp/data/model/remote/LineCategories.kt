package com.jalasoft.routesapp.data.model.remote

import java.io.Serializable

data class LineCategories(
    val id: String = "",
    val nameEng: String = "",
    val nameEsp: String = "",
    val icons: LineCategoryIcons = LineCategoryIcons()
) : Serializable

data class LineCategoryIcons(
    val whiteUrl: String = "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fbus_white.png?alt=media&token=980b407c-2fc7-4fd2-b8da-a5504a7c7f1c",
    val blackUrl: String = "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fbus_black.png?alt=media&token=21c3ba52-27ed-499a-933a-a31c8f2062ba"
): Serializable
