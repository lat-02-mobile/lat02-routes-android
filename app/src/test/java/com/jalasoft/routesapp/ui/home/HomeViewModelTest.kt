package com.jalasoft.routesapp.ui.home

import com.google.maps.android.PolyUtil
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.data.source.*
import com.jalasoft.routesapp.data.source.FakeDirectionsManager
import com.jalasoft.routesapp.data.source.FakeLocalDataBaseManager
import com.jalasoft.routesapp.data.source.FakePlaceManager
import com.jalasoft.routesapp.data.source.FakeRoutesData
import com.jalasoft.routesapp.ui.home.viewModel.HomeViewModel
import com.jalasoft.routesapp.util.Extensions.toLatLong
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
class HomeViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeManager: FakePlaceManager
    private lateinit var fakeDirectionsManager: FakeDirectionsManager
    private lateinit var fakeLocalDataBaseManager: FakeLocalDataBaseManager
    private lateinit var line1: LineRoutePath
    private lateinit var line2: LineRoutePath

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakePlaceManager()
        fakeDirectionsManager = FakeDirectionsManager()
        fakeLocalDataBaseManager = FakeLocalDataBaseManager()
        viewModel = HomeViewModel(fakeManager, fakeDirectionsManager, fakeLocalDataBaseManager)

        val start1 = FakeRoutesData.coordinatesToLocation(-16.52035351419114, -68.12580890707301)
        val end1 = FakeRoutesData.coordinatesToLocation(-16.524285569842718, -68.12298370418992)
        val points1 =
            FakeRoutesData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.points1Array)
        val stops1 = FakeRoutesData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops1Array)
        line1 = LineRoutePath(
            FakeRoutesData.idLine1,
            FakeRoutesData.line1Name,
            FakeRoutesData.line1Category,
            FakeRoutesData.line1Route1Name,
            points1,
            start1,
            end1,
            stops1
        )

        val start2 = FakeRoutesData.coordinatesToLocation(-16.5255314, -68.1254204)
        val end2 = FakeRoutesData.coordinatesToLocation(-16.5241937, -68.1204527)
        val points2 =
            FakeRoutesData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.points2Array)
        val stops2 = FakeRoutesData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops2Array)
        line2 = LineRoutePath(
            FakeRoutesData.idLine2,
            FakeRoutesData.line2Name,
            FakeRoutesData.line2Category,
            FakeRoutesData.line2Route1Name,
            points2,
            start2,
            end2,
            stops2
        )
    }

    @Test
    fun `When the user set the origin location the value should update`() {
        viewModel.setOrigin(PlaceManagerFakeData.fakeLatLngOrigin)
        assertEquals(viewModel.selectedOrigin.value, PlaceManagerFakeData.fakeLatLngOrigin)
    }

    @Test
    fun `When the user set the destination location the value should update`() {
        viewModel.setDestination(PlaceManagerFakeData.fakeLatLngDestination)
        assertEquals(viewModel.selectedDestination.value, PlaceManagerFakeData.fakeLatLngDestination)
    }

    @Test
    fun `Given two stops, when they belong to different transportation categories, then returns all the points to join this stops`() {
        val startLocation = StartLocation(-16.5244779,	-68.1253892)
        val endLocation = EndLocation(-16.5255314, -68.1254204)
        val points = viewModel.fetchDirections(startLocation, endLocation)?.first()?.overviewPolyline?.points
        points?.let {
            assertEquals(PolyUtil.decode(it), FakeRoutesData.directionsPointLst)
        } ?: run {
            assertEquals(false, true)
        }
    }

    @Test
    fun `Given two possible routes, when clear this least, then the possible route list become empty`() {
        val originPoint = FakeRoutesData.coordinatesToLocation(-16.52094, -68.12419)
        val destinationPoint = FakeRoutesData.coordinatesToLocation(-16.52442, -68.12036)

        viewModel.getPossibleRoutes(listOf(line1, line2), originPoint.toLatLong(), destinationPoint.toLatLong())
        viewModel.clearPossibleRoutes()
        assertEquals(viewModel.possibleRoutesList.value, listOf<AvailableTransport>())
    }
}
