package com.jalasoft.routesapp.ui.adminPages.cities.ViewModel

import androidx.lifecycle.Observer
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.source.*
import com.jalasoft.routesapp.ui.adminPages.cities.viewModel.CityAdminViewModel
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
class CitiesAdminViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: CityAdminViewModel
    private lateinit var fakeCityManager: FakeCityManager
    private var city = City("countryName", "id", "idCountry", "-15.11111", "-17.11111", "cityName")

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeCityManager = FakeCityManager()
        viewModel = CityAdminViewModel(fakeCityManager)
    }

    @Test
    fun `Given the first call for fetching cities, when the cities list screen appears, then returns all the cities`() {
        viewModel.fetchCities()
        assertEquals(viewModel._cityList.value, FakeCitiesData.cityAuxAll)
    }

    @Test
    fun `Given a filter criteria, then returns all cities`() {
        viewModel.fetchCities()
        viewModel.applyFilterAndSort()
        assertEquals(viewModel._cityList.value, FakeCitiesData.cityAuxAll)
    }

    @Test
    fun `Given valid and not empty field name it returns an empty String`() {
        val result = viewModel.validateFields("name", "lat", "lng", "id")
        assertEquals("", result)
    }

    @Test
    fun `When trying to save new city and given an not empty field name it returns a true success result`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.successResult.observeForever(observer)
                viewModel.saveNewCity("name", "-15.1111", "-17.1111", "Bolivia", "abc")

                val value = viewModel.successResult.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.successResult.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to save new city and given an empty field name it returns an error String`() {
        runBlocking {
            val observer = Observer<String> {}

            try {
                viewModel.errorMessage.observeForever(observer)
                viewModel.saveNewCity("", "-15.11111", "-17.11111", "countrySelected", "countryId")

                val value = viewModel.errorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.errorMessage.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to update a city and given an not empty field name it returns a true success result`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.successResult.observeForever(observer)
                viewModel.updateCity("cityname", "-15.11111", "-17.55555", "countryname", "abc", city = city)

                val value = viewModel.successResult.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.successResult.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to update a city and given an empty field name it returns an error String`() {
        runBlocking {
            val observer = Observer<String> {}

            try {
                viewModel.errorMessage.observeForever(observer)
                viewModel.updateCity("", "-15.11111", "-17.55555", "countryname", "abc", city = city)

                val value = viewModel.errorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.errorMessage.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to delete a city and given an id city it returns a true success result`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.successResult.observeForever(observer)
                viewModel.deleteCity("1")

                val value = viewModel.successResult.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.successResult.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to delete a city and given an not empty id city it returns an error message`() {
        runBlocking {
            val observer = Observer<String> {}

            try {
                viewModel.errorMessage.observeForever(observer)
                viewModel.deleteCity("")

                val value = viewModel.errorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.errorMessage.removeObserver(observer)
            }
        }
    }
}
