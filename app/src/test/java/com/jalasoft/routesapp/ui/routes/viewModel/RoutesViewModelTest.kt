package com.jalasoft.routesapp.ui.routes.viewModel

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
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
    private lateinit var context: Context

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeRoutesManager()
        viewModel = RoutesViewModel(fakeManager)
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun `Given the first call for fetching routes, when the routes screen appears, then returns all the routes from the current city`() {
        viewModel.fetchRoutes(context)
        assertEquals(viewModel._routesList.value, FakeRoutesData.lines)
    }

    @Test
    fun `Given the first call for fetching routes, when there is a filter criteria, then returns all the routes that match the filter criteria`() {
        viewModel.fetchRoutes(context)
        viewModel.filterRoutes("Line")
        assertEquals(viewModel._routesList.value, FakeRoutesData.lines.filter { it.name.contains("Line") })
    }

    @Test
    fun `Given the first call for fetching routes, when there is a filter criteria that does not match, then returns an empty result`() {
        viewModel.fetchRoutes(context)
        viewModel.filterRoutes("5")
        assertTrue(viewModel._routesList.value!!.isEmpty())
    }
}
