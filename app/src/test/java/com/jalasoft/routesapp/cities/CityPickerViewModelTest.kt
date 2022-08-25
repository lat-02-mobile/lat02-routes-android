package com.jalasoft.routesapp.cities

import com.jalasoft.routesapp.data.source.CountriesFakedata
import com.jalasoft.routesapp.data.source.FakeDataCountryManager
import com.jalasoft.routesapp.ui.cityPicker.viewModel.CityPickerViewModel
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
class CityPickerViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: CityPickerViewModel
    private lateinit var fakeManager: FakeDataCountryManager

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeDataCountryManager()
        viewModel = CityPickerViewModel(fakeManager)
    }

    @Test
    fun `Fetch all the cities`() {
        viewModel.fetchCities()
        assertEquals(viewModel._citiesList.value, CountriesFakedata.cities)
    }

    @Test
    fun `Filter cities by name`() {
        viewModel.fetchCities()
        viewModel.filterCities("sucre")
        assertEquals(viewModel._citiesList.value, CountriesFakedata.cities.filter { it.name == "Sucre" })
    }

    @Test
    fun `Filter cities by country name`() {
        viewModel.fetchCities()
        viewModel.filterCities("bolivia")
        assertEquals(viewModel._citiesList.value, CountriesFakedata.cities.filter { it.country == "Bolivia" })
    }

    @Test
    fun `Filter cities for no result`() {
        viewModel.fetchCities()
        viewModel.filterCities("gfdsfdsa")
        viewModel._citiesList.value?.let { assertEquals(it.isEmpty(), true) }
    }
}
