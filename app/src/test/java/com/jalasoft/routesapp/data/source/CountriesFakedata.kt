package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.CityRoute
import com.jalasoft.routesapp.data.model.remote.Country

object CountriesFakedata {
    val countries = listOf<Country>(
        Country("BO", "Bolivia", "+591"),
        Country("PE", "Peru", "+51"),
        Country("MX", "Mexico", "+52")

    )
    val cities = listOf<City>(
        City("Potosi", "Bolivia", "BO", "+591", "6.5", "-6.5"),
        City("Sucre", "Bolivia", "BO", "+591", "6.4", "-6.4"),
        City("CDMX", "Mexico", "MX", "+52", "-6.3", "6.3")
    )
    val cityRoutes = listOf<CityRoute>(
        CityRoute(name = "Potosi", lat = "6.5", lng = "-6.5"),
        CityRoute(name = "Sucre", lat = "6.4", lng = "-6.4"),
        CityRoute(name = "CDMX", lat = "-6.3", lng = "6.3")
    )
}
