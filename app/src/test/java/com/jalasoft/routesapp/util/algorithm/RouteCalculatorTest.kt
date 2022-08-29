package com.jalasoft.routesapp.util.algorithm

import com.jalasoft.routesapp.data.model.remote.CityRouteForAlgorithm
import com.jalasoft.routesapp.data.model.remote.Route
import com.jalasoft.routesapp.data.model.remote.TransportMethod
import com.jalasoft.routesapp.data.source.CountriesFakedata
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, manifest = Config.NONE)
@HiltAndroidTest
class RouteCalculatorTest : TestCase() {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    lateinit var cityRouteForAlgorithm: CityRouteForAlgorithm

    @Before
    public override fun setUp() {
        super.setUp()
        val start1 = RouteCalculator.coordinatesToLocation(-16.52035351419114, -68.12580890707301)

        val points1 = RouteCalculator.arrayToMutableListOfLocation(CountriesFakedata.points1Array)
        val stops1 = RouteCalculator.arrayToMutableListOfLocation(CountriesFakedata.stops1Array)
        val lines = listOf(Route("Line 1", points1, start1, stops1))
        val transportMethod1 = TransportMethod("Bus", lines)

        val start2 = RouteCalculator.coordinatesToLocation(-16.5255314, -68.1254204)
        val stops2 = RouteCalculator.arrayToMutableListOfLocation(CountriesFakedata.stops2Array)
        val points2 = RouteCalculator.arrayToMutableListOfLocation(CountriesFakedata.points2Array)

        val line2 = listOf(Route("246", points2, start2, stops2))
        val transportMethod2 = TransportMethod("Mini", line2)

        hiltRule.inject()
        cityRouteForAlgorithm = CityRouteForAlgorithm(listOf(transportMethod1, transportMethod2), name = "La Paz")
    }

    @Test
    fun `Given a list of lines and an origin an destination point when the line stops are near the given points then it returns a list of single lines`() {
        val originPoint = RouteCalculator.coordinatesToLocation(-16.52094, -68.12419)
        val destinationPoint = RouteCalculator.coordinatesToLocation(-16.52355, -68.12269)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(cityRouteForAlgorithm, destinationPoint, originPoint, minDistance, minDistanceBtwStops)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `Given a list of lines and an origin an destination point when the line stops are near two lines then it returns a list of lines with two transport`() {
        val originPoint = RouteCalculator.coordinatesToLocation(-16.52094, -68.12419)
        val destinationPoint = RouteCalculator.coordinatesToLocation(-16.52442, -68.12036)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(cityRouteForAlgorithm, destinationPoint, originPoint, minDistance, minDistanceBtwStops)
        if (result.isNotEmpty()) {
            assertNotNull(result.first().connectionPoint)
        } else {
            fail()
        }
    }
}
