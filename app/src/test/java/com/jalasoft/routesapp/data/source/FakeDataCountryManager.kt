package com.jalasoft.routesapp.data.source

import com.google.firebase.firestore.DocumentReference
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.CityRoute
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.data.remote.managers.CountryRepository

class FakeDataCountryManager : CountryRepository {

    override suspend fun getAllCountries(): List<Country> {
        return CountriesFakedata.countries
    }

    override suspend fun getAllCityRoutes(): List<CityRoute> {
        return CountriesFakedata.cityRoutes
    }

    override suspend fun getAllCities(): List<City> {
        return CountriesFakedata.cities
    }

    override suspend fun getCityRoutesByCountry(reference: List<DocumentReference>): List<CityRoute> {
        return CountriesFakedata.cityRoutes
    }
}
