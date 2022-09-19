package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.local.Location
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper

object FakeTourPointData {
    private val location1 = GoogleMapsHelper.coordinatesToLocation(-16.52035351419114, -68.12580890707301)
    private val tourPoint1 = TourPointPath("1", "City 1", "13 street", location1, "", "Museum")
    private val location2 = GoogleMapsHelper.coordinatesToLocation(-16.524285569842718, -68.12298370418992)
    private val tourPoint2 = TourPointPath("2", "City 2", "14 street", location2, "", "Park")
    val tourPoints = listOf(tourPoint1, tourPoint2)
    private val locationLocal = Location(-16.52035351419114, -68.12580890707301)
    private val tourPointEntity = TourPointEntity("1", "City 1", "13 street", locationLocal, "http", "park")
    private val touPointCategoryEntity = TourPointsCategoryEntity("1", "park", "parque")
    val tourPointsEntity = listOf(tourPointEntity)
    val tourPointsCategoryEntity = listOf(touPointCategoryEntity)
}
