package com.jalasoft.routesapp.util.algorithm

import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.data.source.RouteAlgorithmFakeData
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

    private lateinit var lines: List<LinePath>

    @Before
    public override fun setUp() {
        super.setUp()
        val start1 = RouteAlgorithmFakeData.coordinatesToLocation(-16.52035351419114, -68.12580890707301)
        val points1 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.points1Array)
        val stops1 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops1Array)
        val line1 = LinePath("Line 1", "Bus", points1, start1, stops1)

        val start2 = RouteAlgorithmFakeData.coordinatesToLocation(-16.5255314, -68.1254204)
        val points2 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.points2Array)
        val stops2 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops2Array)

        val line2 = LinePath("246", "Mini", points2, start2, stops2)
        hiltRule.inject()
        lines = listOf(line1, line2)
    }

    @Test
    fun `Given a list of lines and an origin an destination point when the line stops are near the given points then it returns a list of single lines`() {
        val originPoint = RouteAlgorithmFakeData.coordinatesToLocation(-16.52094, -68.12419)
        val destinationPoint = RouteAlgorithmFakeData.coordinatesToLocation(-16.52355, -68.12269)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(lines, destinationPoint, originPoint, minDistance, minDistanceBtwStops)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `Given a list of lines and an origin an destination point when the line stops are near two lines then it returns a list of lines with two transport`() {
        val originPoint = RouteAlgorithmFakeData.coordinatesToLocation(-16.52094, -68.12419)
        val destinationPoint = RouteAlgorithmFakeData.coordinatesToLocation(-16.52442, -68.12036)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(lines, destinationPoint, originPoint, minDistance, minDistanceBtwStops)
        if (result.isNotEmpty()) {
            assertNotNull(result.first().connectionPoint)
        } else {
            fail()
        }
    }
}
