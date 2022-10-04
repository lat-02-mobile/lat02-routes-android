package com.jalasoft.routesapp.ui.tourPoints.viewModel

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.jalasoft.routesapp.data.source.FakeTourPointData
import com.jalasoft.routesapp.data.source.FakeTourPointsLocalManager
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
        viewModel.fetchTourPoints()
        assertEquals(viewModel._tourPointsList.value, FakeTourPointData.tourPoints)
    }
}
