package com.jalasoft.routesapp.ui.adminPages.routes.viewModel

import androidx.lifecycle.Observer
import com.jalasoft.routesapp.data.model.local.LineRouteAux
import com.jalasoft.routesapp.data.source.FakeRoutesData
import com.jalasoft.routesapp.data.source.FakeRoutesManager
import com.jalasoft.routesapp.data.source.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
class RoutesAdminViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: RoutesAdminViewModel
    private lateinit var fakeRoutesManager: FakeRoutesManager

    private val lineId = "line_id"

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeRoutesManager = FakeRoutesManager()
        viewModel = RoutesAdminViewModel(fakeRoutesManager)
    }

    @Test
    fun `Given the first call for fetching routes, when the route list screen appears, then returns all the routes`() {
        viewModel.getAllRoutesForLine(lineId)
        assertEquals(viewModel.successFetching.value, FakeRoutesData.lineRouteInfo)
    }

    @Test
    fun `When trying to save new route and given the correct values it returns a success result`() {
        runBlocking {
            val observer = Observer<Unit> {}
            try {
                viewModel.successSave.observeForever(observer)
                viewModel.saveRoute(LineRouteAux(lineId), true)
                val value = viewModel.successSave.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.successSave.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to update a route and given the correct values it returns a true success result`() {
        runBlocking {
            val observer = Observer<Unit> {}
            try {
                viewModel.successSave.observeForever(observer)
                viewModel.saveRoute(LineRouteAux(lineId), false)

                val value = viewModel.successSave.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.successSave.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to delete a route and given its ID then it returns a success result`() {
        runBlocking {
            val observer = Observer<Unit> {}
            try {
                viewModel.successSave.observeForever(observer)
                viewModel.deleteRoute("1")

                val value = viewModel.successSave.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.successSave.removeObserver(observer)
            }
        }
    }
}
