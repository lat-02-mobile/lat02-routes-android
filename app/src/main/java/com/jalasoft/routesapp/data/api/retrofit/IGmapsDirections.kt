package com.jalasoft.routesapp.data.api.retrofit

import com.jalasoft.routesapp.data.api.models.gmaps.DirectionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IGmapsDirections {

    @GET("maps/api/directions/json")
    suspend fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") apiKey: String
    ): Response<DirectionResponse>
}
