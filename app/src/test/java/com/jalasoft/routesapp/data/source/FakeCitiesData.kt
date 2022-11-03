package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.City

object FakeCitiesData {
    private val cityAux = City("Bolivia", "city1", "country1", "6.5", "-6.5", "Sucre")
    val cityAuxList = listOf(cityAux)

    val cityAll1 = City("Bolivia", "city1", "country1", "6.5", "-6.5", "Sucre")
    val cityAll2 = City("Bolivia", "city2", "country1", "6.4", "-6.4", "Potosi")
    val cityAll3 = City("Mexico", "city3", "country2", "-6.3", "6.3", "CDMX")

    val cityAuxAll = listOf(cityAll1, cityAll2, cityAll3)
}
