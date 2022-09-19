package com.jalasoft.routesapp.ui.auth.login.viewModel

import androidx.lifecycle.Observer
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.source.FakeDataUserManager
import com.jalasoft.routesapp.data.source.getOrAwaitValue
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
class LoginViewModelTest : TestCase() {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeManager: FakeDataUserManager

    private val email: String = "test@gmail.com"
    private var password: String = "test.test"
    private val users: MutableList<User> = mutableListOf()

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeDataUserManager(users)
        viewModel = LoginViewModel(fakeManager)
    }

    @Test
    fun `Given an empty or invalid format of email it returns error message`() {
        val emailTwo = "example"
        fakeManager = FakeDataUserManager(users)
        val observer = Observer<String> {}
        viewModel.validateFields(emailTwo, password)

        try {
            viewModel.errorMessage.observeForever(observer)
            viewModel.validateFields(emailTwo, password)

            val value = viewModel.errorMessage.getOrAwaitValue()
            assertNotNull(value)
        } finally {
            viewModel.errorMessage.removeObserver(observer)
        }
    }

    @Test
    fun `Given an empty password or invalid fields it returns error message`() {
        val emailTwo = "test@gmail.com"
        val passwordTwo = ""
        runBlocking {
            viewModel.validateFields(email, password)
            viewModel.validateFields(emailTwo, emailTwo)
            val result = fakeManager.signInWithEmailAndPassword(email, password)
            val resultTwo = fakeManager.signInWithEmailAndPassword(emailTwo, passwordTwo)
            assertNotSame(result, resultTwo)
            assertNotNull(result)
            assertNotNull(resultTwo)
        }
    }

    @Test
    fun `Given a correct email and password validates and goes to homepage`() {
        val emailTwo = "test@gmail.com"
        val passwordTwo = "test.test"
        runBlocking {
            viewModel.validateFields(email, password)
            val result = fakeManager.signInWithEmailAndPassword(email, password)
            val resultTwo = fakeManager.signInWithEmailAndPassword(emailTwo, passwordTwo)
            assertEquals(result.data.toString(), resultTwo.data.toString())
            val observer = Observer<Boolean> {}
            try {
                viewModel.loginIsSuccessful.observeForever(observer)
                val value = viewModel.loginIsSuccessful.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.loginIsSuccessful.removeObserver(observer)
            }
        }
    }
}
