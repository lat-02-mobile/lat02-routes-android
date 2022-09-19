package com.jalasoft.routesapp.data.model.remote

import java.io.Serializable

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val type: Int? = null,
    val typeLogin: Int? = null,
    val createdAt: Double? = null,
    val updatedAt: Double? = null
) : Serializable
