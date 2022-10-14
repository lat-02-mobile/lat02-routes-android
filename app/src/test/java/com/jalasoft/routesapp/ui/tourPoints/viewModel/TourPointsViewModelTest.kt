package com.jalasoft.routesapp.ui.tourPoints.viewModel

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.jalasoft.routesapp.data.source.FakeTourPointData
import com.jalasoft.routesapp.data.source.FakeTourPointsLocalManager
import com.jalasoft.routesapp.util.helpers.FilterType
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
class TourPointsViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: TourPointsViewModel
    private lateinit var fakeManager: FakeTourPointsLocalManager
    private lateinit var context: Context

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeTourPointsLocalManager()
        viewModel = TourPointsViewModel(fakeManager)
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun `Given the first call for fetching tourPoints, when the tourPoints list screen appears, then returns all the tourPoints from the current city`() {
        viewModel.fetchTourPoints("")
        assertEquals(viewModel._tourPointsList.value, FakeTourPointData.tourPoints)
    }

    @Test
    fun `Given a filter criteria, then returns all Lines`() {
        viewModel.fetchTourPoints("")
        viewModel.sortQuery = FilterType.ALL
        viewModel.applyFilterAndSort()
        assertEquals(viewModel._tourPointsList.value, FakeTourPointData.tourPoints)
    }

    @Test
    fun `Given a filter category criteria, then returns all tourPoints in order`() {
        viewModel.fetchTourPoints("")
        viewModel.sortQuery = FilterType.CATEGORY
        viewModel.applyFilterAndSort()
        val sortedTourPoints = viewModel.originalList.sortedBy { it.categoryName }
        assertEquals(viewModel._tourPointsList.value, sortedTourPoints)
    }

    @Test
    fun `Given a filter category and query criteria, then returns all the filtered tourPoints in order`() {
        viewModel.fetchTourPoints("")
        viewModel.sortQuery = FilterType.CATEGORY
        viewModel.searchQuery = "City"
        viewModel.applyFilterAndSort()
        val sortedTourPoints = viewModel.originalList.sortedBy { it.categoryName }
        assertEquals(viewModel._tourPointsList.value, sortedTourPoints)
    }
}
