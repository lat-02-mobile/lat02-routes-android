package com.jalasoft.routesapp.util.algorithm

import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.data.source.FakeRoutesData
import com.jalasoft.routesapp.data.source.FakeRoutesData.arrayToMutableListOfLocation
import com.jalasoft.routesapp.data.source.FakeRoutesData.coordinatesToLocation
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

    private lateinit var lines: List<LineRoutePath>

    @Before
    public override fun setUp() {
        super.setUp()
        val start1 = coordinatesToLocation(-16.52035351419114, -68.12580890707301)
        val end1 = coordinatesToLocation(-16.524285569842718, -68.12298370418992)
        val points1 = arrayToMutableListOfLocation(RouteAlgorithmFakeData.points1Array)
        val stops1 = arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops1Array)
        val line1 = LineRoutePath(
            FakeRoutesData.idLine1,
            FakeRoutesData.line1Name,
            FakeRoutesData.line1Category,
            FakeRoutesData.line1Route1Name,
            points1,
            start1,
            end1,
            stops1
        )

        val start2 = coordinatesToLocation(-16.5255314, -68.1254204)
        val end2 = coordinatesToLocation(-16.5241937, -68.1204527)
        val points2 = arrayToMutableListOfLocation(RouteAlgorithmFakeData.points2Array)
        val stops2 = arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops2Array)
        val line2 = LineRoutePath(
            FakeRoutesData.idLine2,
            FakeRoutesData.line2Name,
            FakeRoutesData.line2Category,
            FakeRoutesData.line2Route1Name,
            points2,
            start2,
            end2,
            stops2
        )

        val start3 = coordinatesToLocation(-16.5206262, -68.1227148)
        val end3 = coordinatesToLocation(-16.5244779, -68.1253892)
        val points3 = arrayToMutableListOfLocation(RouteAlgorithmFakeData.points3Array)
        val stops3 = arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops3Array)
        val line3 = LineRoutePath(
            FakeRoutesData.idLine3,
            FakeRoutesData.line3Name,
            FakeRoutesData.line3Category,
            FakeRoutesData.line3Route1Name,
            points3,
            start3,
            end3,
            stops3
        )

        hiltRule.inject()
        lines = listOf(line1, line2, line3)
    }

    @Test
    fun `Given a list of lines and an origin and destination point when the line stops are near the given points then it returns a list of single lines`() {
        val originPoint = coordinatesToLocation(-16.52078, -68.12344)
        val destinationPoint = coordinatesToLocation(-16.52455, -68.12269)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(lines, destinationPoint, originPoint, minDistance, minDistanceBtwStops)
        assertTrue(result.isNotEmpty())
        assertTrue(RouteAlgorithmFakeData.compareResults(result, RouteAlgorithmFakeData.result1_4))
    }

    @Test
    fun `Given a list of lines and an origin and destination point when the line stops are near two lines then it returns a list of lines with two transport`() {
        val originPoint = coordinatesToLocation(-16.52094, -68.12419)
        val destinationPoint = coordinatesToLocation(-16.52442, -68.12036)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(lines, destinationPoint, originPoint, minDistance, minDistanceBtwStops)
        if (result.isNotEmpty()) {
            val hasConnectionPoint = result.filter {
                it.connectionPoint != null
            }
            assertTrue(hasConnectionPoint.isNotEmpty())
            assertTrue(RouteAlgorithmFakeData.compareResults(result, RouteAlgorithmFakeData.result2))
        } else {
            fail()
        }
    }

    @Test
    fun `Given a list of lines and an origin and destination point when the points are far away from any line`() {
        val originPoint = coordinatesToLocation(-16.52221, -68.12749)
        val destinationPoint = coordinatesToLocation(-16.52442, -68.12036)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(lines, destinationPoint, originPoint, minDistance, minDistanceBtwStops)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `Given a list of lines and an origin and destination point when there are one and two transportation lines then it returns a list with single and double lines`() {
        val originPoint = coordinatesToLocation(-16.52078, -68.12344)
        val destinationPoint = coordinatesToLocation(-16.52455, -68.12269)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(lines, destinationPoint, originPoint, minDistance, minDistanceBtwStops)

        val singleLineExist = result.filter {
            it.connectionPoint == null
        }
        val doubleLineExists = result.filter {
            it.connectionPoint != null
        }
        assertTrue(singleLineExist.isNotEmpty() && doubleLineExists.isNotEmpty())
        assertTrue(RouteAlgorithmFakeData.compareResults(result, RouteAlgorithmFakeData.result1_4))
    }
}
