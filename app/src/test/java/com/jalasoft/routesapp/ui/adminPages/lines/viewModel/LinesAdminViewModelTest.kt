package com.jalasoft.routesapp.ui.adminPages.lines.viewModel

import androidx.lifecycle.Observer
import com.jalasoft.routesapp.data.source.*
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
class LinesAdminViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: LinesAdminViewModel
    private lateinit var fakeRoutesManager: FakeRoutesManager
    private lateinit var fakeCityManager: FakeCityManager

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeRoutesManager = FakeRoutesManager()
        fakeCityManager = FakeCityManager()
        viewModel = LinesAdminViewModel(fakeRoutesManager, fakeCityManager)
    }

    @Test
    fun `Given the first call for fetching lines, when the lines list screen appears, then returns all the lines`() {
        viewModel.fetchLines()
        assertEquals(viewModel._lineList.value, FakeRoutesData.lineAuxList)
    }

    @Test
    fun `Given a filter criteria, then returns all Lines`() {
        viewModel.fetchLines()
        viewModel.applyFilterAndSort()
        assertEquals(viewModel._lineList.value, FakeRoutesData.lineAuxList)
    }

    @Test
    fun `Given valid and not empty field name it returns an empty String`() {
        val result = viewModel.validateFields("name")
        assertEquals("", result)
    }

    @Test
    fun `When trying to save new line and given an not empty field name it returns a true success result`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.successResult.observeForever(observer)
                viewModel.saveNewLine("name")

                val value = viewModel.successResult.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.successResult.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to save new line and given an empty field name it returns an error String`() {
        runBlocking {
            val observer = Observer<String> {}

            try {
                viewModel.errorMessage.observeForever(observer)
                viewModel.saveNewLine("")

                val value = viewModel.errorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.errorMessage.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to update a line and given an not empty field name it returns a true success result`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.successResult.observeForever(observer)
                viewModel.updateLine("1", "name")

                val value = viewModel.successResult.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.successResult.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to update a line and given an empty field name it returns an error String`() {
        runBlocking {
            val observer = Observer<String> {}

            try {
                viewModel.errorMessage.observeForever(observer)
                viewModel.updateLine("1", "")

                val value = viewModel.errorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.errorMessage.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to delete a line and given an id line it returns a true success result`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.successResult.observeForever(observer)
                viewModel.deleteLine("1")

                val value = viewModel.successResult.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.successResult.removeObserver(observer)
            }
        }
    }

    @Test
    fun `When trying to delete a line and given an not empty id line it returns an error message`() {
        runBlocking {
            val observer = Observer<String> {}

            try {
                viewModel.errorMessage.observeForever(observer)
                viewModel.deleteLine("")

                val value = viewModel.errorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.errorMessage.removeObserver(observer)
            }
        }
    }
}
