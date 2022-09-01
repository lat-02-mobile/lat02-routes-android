package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository

class FakeCityManager : CityRepository {

    override suspend fun getAllCities(): List<City> {
        return CityManagerFakeData.cities
    }
}
