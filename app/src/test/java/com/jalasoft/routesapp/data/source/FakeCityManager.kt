package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository
import com.jalasoft.routesapp.ui.cityPicker.CityManagerFakeData
import com.jalasoft.routesapp.util.Response

class FakeCityManager : CityRepository {

    override suspend fun getAllCities(): List<City> {
        return CityManagerFakeData.cities
    }

    override suspend fun getAllCountries(): List<Country> {
        return CityManagerFakeData.countries
    }

    override suspend fun addNewCity(
        country: String,
        idCountry: String,
        lat: String,
        lng: String,
        name: String
    ): Response<String> {
        return Response.Success(name)
    }

    override suspend fun updateCity(
        country: String,
        id: String,
        idCountry: String,
        lat: String,
        lng: String,
        name: String
    ): Response<String> {
        return Response.Success(id)
    }

    override suspend fun deleteCity(id: String): Response<String> {
        return Response.Success(id)
    }
}
