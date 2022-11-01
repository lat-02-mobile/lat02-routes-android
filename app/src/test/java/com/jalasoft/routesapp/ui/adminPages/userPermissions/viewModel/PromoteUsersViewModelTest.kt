package com.jalasoft.routesapp.ui.adminPages.userPermissions.viewModel

import androidx.lifecycle.Observer
import com.jalasoft.routesapp.data.remote.interfaces.UserRepository
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
class PromoteUsersViewModelTest : TestCase() {
    @get :Rule
    val hiltRule = HiltAndroidRule(this)
    private lateinit var viewModel: PromoteUsersViewModel
    private lateinit var fakeUserManager: UserRepository

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeUserManager = FakeDataUserManager()
        viewModel = PromoteUsersViewModel(fakeUserManager)
    }

    @Test
    fun `Given the first call for fetching lines, when the lines list screen appears, then returns all the lines`() {
        viewModel.fetchUsers()
        assertEquals(viewModel._usersList.value, FakeUserData.userList)
    }

    @Test
    fun `Given a filter criteria, then returns all Lines`() {
        viewModel.fetchUsers()
        viewModel.applyFilter()
        assertEquals(viewModel._usersList.value, FakeUserData.userList)
    }

    @Test
    fun `Given a user to promote, then returns true`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.successResult.observeForever(observer)
                viewModel.promoteUser(FakeUserData.user1)

                val value = viewModel.successResult.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.successResult.removeObserver(observer)
            }
        }
    }

    @Test
    fun `Given a user to revoke permission, then returns true`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.successResult.observeForever(observer)
                viewModel.revokeUserPermission(FakeUserData.user1)

                val value = viewModel.successResult.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.successResult.removeObserver(observer)
            }
        }
    }
}
