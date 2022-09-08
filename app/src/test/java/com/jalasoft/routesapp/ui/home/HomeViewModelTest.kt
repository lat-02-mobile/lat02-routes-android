package com.jalasoft.routesapp.ui.home

import com.jalasoft.routesapp.data.source.FakePlaceManager
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

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakePlaceManager()
        viewModel = HomeViewModel(fakeManager)
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
}
