package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository

class FakeDataCountryManager : CityRepository {

    override suspend fun getAllCities(): List<City> {
        return CountriesFakeData.cities
    }
}
