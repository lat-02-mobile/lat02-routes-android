package com.jalasoft.routesapp.data.api.retrofit

import com.jalasoft.routesapp.data.api.models.gmaps.GMapsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IGmapsService {
    @GET("maps/api/place/textsearch/json")
    suspend fun getPlaces(
        @Query("query") placeCriteria: String,
        @Query("key") keyMap: String
    ): Response<GMapsResponse>
}
