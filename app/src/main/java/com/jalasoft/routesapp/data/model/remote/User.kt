package com.jalasoft.routesapp.data.model.remote

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val type: Int? = null,
    val typeLogin: Int? = null,
    @ServerTimestamp
    val createAt: Date? = Date(),
    @ServerTimestamp
    val updateAt: Date? = Date()
) : Serializable
