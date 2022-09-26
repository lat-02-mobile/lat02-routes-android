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
    fun `Given the first call for fetching the lines, when the line screen appears, then returns all the Lines from the current city`() {
        viewModel.fetchLines(context)
        assertEquals(viewModel.routesList.value, FakeRoutesData.lineInfo)
    }

    @Test
    fun `Given the first call for fetching lines, when there is a filter criteria, then returns all the Lines that match the filter criteria`() {
        viewModel.fetchLines(context)
        viewModel.filterLines("Line")
        assertEquals(viewModel.routesList.value, FakeRoutesData.lineInfo.filter { it.name.contains("Line") })
    }

    @Test
    fun `Given the first call for fetching lines, when there is a filter criteria that does not match, then returns an empty result`() {
        viewModel.fetchLines(context)
        viewModel.filterLines("5")
        assertTrue(viewModel.routesList.value!!.isEmpty())
    }
}
