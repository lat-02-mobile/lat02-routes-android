package com.jalasoft.routesapp.ui.adminPages.routeEditor

import com.jalasoft.routesapp.data.source.FakeRoutesManager
import com.jalasoft.routesapp.ui.adminPages.routeEditor.viewModel.RouteEditorViewModel
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
class RouteEditorViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: RouteEditorViewModel
    private lateinit var fakeManager: FakeRoutesManager

    private val routeId = "route_id"

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeRoutesManager()
        viewModel = RouteEditorViewModel(fakeManager)
    }

    @Test
    fun `Given the route points and stops edited, when the user saves the data then firebase returns success save`() {
        viewModel.saveRouteDetails(routeId, listOf(), listOf())
    }
}
