package com.jalasoft.routesapp.ui.routes.viewModel

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.google.maps.android.PolyUtil
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.source.FakeDirectionsManager
import com.jalasoft.routesapp.data.source.FakeRoutesData
import com.jalasoft.routesapp.data.source.FakeRoutesManager
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
class RoutesViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: RoutesViewModel
    private lateinit var fakeManager: FakeRoutesManager
    private lateinit var fakeDirectionsManager: FakeDirectionsManager
    private lateinit var context: Context

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeRoutesManager()
        fakeDirectionsManager = FakeDirectionsManager()
        viewModel = RoutesViewModel(fakeManager, fakeDirectionsManager)
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun `Given the first call for fetching routes, when the routes screen appears, then returns all the routes from the current city`() {
        viewModel.fetchRoutes(context)
        assertEquals(viewModel.routesList.value, FakeRoutesData.lines)
    }

    @Test
    fun `Given the first call for fetching routes, when there is a filter criteria, then returns all the routes that match the filter criteria`() {
        viewModel.fetchRoutes(context)
        viewModel.filterRoutes("Line")
        assertEquals(viewModel.routesList.value, FakeRoutesData.lines.filter { it.name.contains("Line") })
    }

    @Test
    fun `Given the first call for fetching routes, when there is a filter criteria that does not match, then returns an empty result`() {
        viewModel.fetchRoutes(context)
        viewModel.filterRoutes("5")
        assertTrue(viewModel.routesList.value!!.isEmpty())
    }

    @Test
    fun `Given two stops, when they belong to different transportation categories, then returns all the points to join this stops`() {
        val startLocation = StartLocation(-16.5244779,	-68.1253892)
        val endLocation = EndLocation(-16.5255314, -68.1254204)
        viewModel.fetchDirections(startLocation, endLocation)
        val points = viewModel.directionsList.value?.first()?.overviewPolyline?.points
        points?.let {
            assertEquals(PolyUtil.decode(it), FakeRoutesData.directionsPointLst)
        } ?: run {
            assertEquals(false, true)
        }
    }
}
