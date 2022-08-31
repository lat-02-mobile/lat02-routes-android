package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.CityRoute
import com.jalasoft.routesapp.data.model.remote.Country

object CountriesFakeData {
    val countries = listOf<Country>(
        Country("BO", "Bolivia", "+591"),
        Country("PE", "Peru", "+51"),
        Country("MX", "Mexico", "+52")

    )
    val cities = listOf(
        City("Bolivia", "city1", "country1", "6.5", "-6.5", "Sucre"),
        City("Bolivia", "city2", "country1", "6.4", "-6.4", "Potosi"),
        City("Mexico", "city3", "country2", "-6.3", "6.3", "CDMX")
    )
    val cityRoutes = listOf<CityRoute>(
        CityRoute(name = "Potosi", lat = "6.5", lng = "-6.5"),
        CityRoute(name = "Sucre", lat = "6.4", lng = "-6.4"),
        CityRoute(name = "CDMX", lat = "-6.3", lng = "6.3")
    )
}
