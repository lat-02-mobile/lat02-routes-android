package com.jalasoft.routesapp.ui.cityPicker

import android.content.Context
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import com.jalasoft.routesapp.data.local.room.interfaces.RouteLocalRepository
import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.source.*
import com.jalasoft.routesapp.ui.cityPicker.viewModel.SplashScreenViewModel
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
class SplashScreenViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: SplashScreenViewModel
    private lateinit var fakeLocalDataBaseManager: FakeLocalDataBaseManager
    private lateinit var fakeRoutesManager: FakeRoutesManager
    private lateinit var fakeTourPointsManager: FakeTourPointsManager
    private lateinit var fakeTourPointLocalRepository: TourPointLocalRepository
    private lateinit var fakeRouteLocalRepository: RouteLocalRepository
    private lateinit var context: Context

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeRoutesManager = FakeRoutesManager()
        fakeTourPointsManager = FakeTourPointsManager()
        fakeLocalDataBaseManager = FakeLocalDataBaseManager()
        fakeTourPointLocalRepository = FakeTourPointsLocalManager()
        fakeRouteLocalRepository = FakeRoutesLocalManager()
        viewModel = SplashScreenViewModel(fakeRoutesManager, fakeTourPointsManager, fakeTourPointLocalRepository, fakeRouteLocalRepository, fakeLocalDataBaseManager)
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun `When the user change his city it will download all the necessary information and store it locally`() {
        val observer = Observer<Boolean> {}

        try {
            viewModel.dataSaved.observeForever(observer)
            viewModel.getDataAndSaveLocally(context)

            val value = viewModel.dataSaved.getOrAwaitValue()
            assertTrue(value)
        } finally {
            viewModel.dataSaved.removeObserver(observer)
        }
    }
}
