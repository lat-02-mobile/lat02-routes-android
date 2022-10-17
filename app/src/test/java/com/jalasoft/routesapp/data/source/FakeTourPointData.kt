package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.local.Location
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.data.model.remote.TourPointsCategory
import com.jalasoft.routesapp.data.source.FakeRoutesData.coordinatesToLocation

object FakeTourPointData {
    private val location1 = coordinatesToLocation(-16.52035351419114, -68.12580890707301)
    private val tourPointCategory1 = TourPointsCategory("1", "", "Hospital", "Hospital")
    private val tourPointCategory2 = TourPointsCategory("2", "", "Airport", "Aeropuerto")
    private val tourPoint1 = TourPointPath("1", "City 1", "13 street", location1, "", "", tourPointCategory1, "Hospital")
    private val location2 = coordinatesToLocation(-16.524285569842718, -68.12298370418992)
    private val tourPoint2 = TourPointPath("2", "City 2", "14 street", location2, "", "", tourPointCategory2, "Airport")
    val tourPoints = listOf(tourPoint1, tourPoint2)
    private val locationLocal = Location(-16.52035351419114, -68.12580890707301)
    private val tourPointEntity = TourPointEntity("1", "City 1", "13 street", locationLocal, "http", "park", "1", "1")
    private val touPointCategoryEntity = TourPointsCategoryEntity("1", "park", "parque", "2")
    val tourPointsEntity = listOf(tourPointEntity)
    val tourPointsCategoryEntity = listOf(touPointCategoryEntity)
}
