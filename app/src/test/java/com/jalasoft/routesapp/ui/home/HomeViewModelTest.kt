package com.jalasoft.routesapp.ui.home

import com.google.maps.android.PolyUtil
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.source.FakeDirectionsManager
import com.jalasoft.routesapp.data.source.FakeLocalDataBaseManager
import com.jalasoft.routesapp.data.source.FakePlaceManager
import com.jalasoft.routesapp.data.source.FakeRoutesData
import com.jalasoft.routesapp.ui.home.viewModel.HomeViewModel
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
    private lateinit var fakeLocalDatabaseManager: FakeLocalDataBaseManager

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakePlaceManager()
        fakeDirectionsManager = FakeDirectionsManager()
        fakeLocalDatabaseManager = FakeLocalDataBaseManager()
        viewModel = HomeViewModel(fakeManager, fakeDirectionsManager, fakeLocalDatabaseManager)
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
}
