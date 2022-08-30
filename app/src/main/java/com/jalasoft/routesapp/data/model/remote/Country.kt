package com.jalasoft.routesapp.data.model.remote

import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

data class Country(
    var code: String = "",
    var name: String = "",
    var phone: String = "",
    var cities: List<DocumentReference> = listOf()
) : Serializable
