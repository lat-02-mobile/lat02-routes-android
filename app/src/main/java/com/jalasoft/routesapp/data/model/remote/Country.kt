package com.jalasoft.routesapp.data.model.remote

data class Country(
    val Code: String,
    val Name: String,
    val Phone: String,
    val Cities: List<String>
)
