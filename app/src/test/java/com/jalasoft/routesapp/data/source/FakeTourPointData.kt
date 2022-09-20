package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.data.source.FakeRoutesData.coordinatesToLocation

object FakeTourPointData {
    private val location1 = coordinatesToLocation(-16.52035351419114, -68.12580890707301)
    private val tourPoint1 = TourPointPath("1", "City 1", "13 street", location1, "", "Museum")
    private val location2 = coordinatesToLocation(-16.524285569842718, -68.12298370418992)
    private val tourPoint2 = TourPointPath("2", "City 2", "14 street", location2, "", "Park")
    val tourPoints = listOf(tourPoint1, tourPoint2)
}
