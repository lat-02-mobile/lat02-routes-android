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

    val stops1Array = listOf(
        listOf(-16.52130602845841, -68.12417648825397),
        listOf(-16.521670987319112, -68.12320625310048),
        listOf(-16.522451435494332, -68.12218135682076),
        listOf(-16.523780248849537, -68.12235510114752),
        listOf(-16.524285569842718, -68.12298370418992)
    )

    val points1Array = listOf(
        listOf(-16.520939322501413, -68.12557074070023),
        listOf(-16.521062847351256, -68.12514516472181),
        listOf(-16.52130602845841, -68.12417648825397),
        listOf(-16.521670987319112, -68.12320625310048),
        listOf(-16.52197231180913, -68.12260107624422),
        listOf(-16.522451435494332, -68.12218135682076),
        listOf(-16.523261825566387, -68.12214426533951),
        listOf(-16.523703514803486, -68.1221403609752),
        listOf(-16.523780248849537, -68.12235510114752),
        listOf(-16.524002964559173, -68.12266159393164),
        listOf(-16.524285569842718, -68.12298370418992)
    )

    val points2Array = listOf(
        listOf(-16.5255314, -68.1254204),
        listOf(-16.5247497,	-68.1251629),
        listOf(-16.5247755,	-68.1246533),
        listOf(-16.5251612,	-68.1243314),
        listOf(-16.5251046,	-68.1238218),
        listOf(-16.5246006,	-68.1232156),
        listOf(-16.5245543,	-68.1218155),
        listOf(-16.5247286,	-68.1216115),
        listOf(-16.5241937,	-68.1204527)
    )

    val stops2Array = listOf(
        listOf(-16.5255314, -68.1254204),
        listOf(-16.5246006,	-68.1232156),
        listOf(-16.5241937,	-68.1204527)
    )
}
