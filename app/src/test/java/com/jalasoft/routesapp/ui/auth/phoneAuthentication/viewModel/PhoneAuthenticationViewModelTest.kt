package com.jalasoft.routesapp.ui.auth.phoneAuthentication.viewModel
import android.app.Activity
import androidx.lifecycle.Observer
import com.jalasoft.routesapp.MainActivity
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
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, manifest = Config.NONE)
@HiltAndroidTest
class PhoneAuthenticationViewModelTest : TestCase() {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Mock
    lateinit var viewModel: PhoneAuthenticationViewModel
    lateinit var fakeManager: FakeDataUserManager
    var users: MutableList<User> = mutableListOf()
    private var phoneNumber: String = "2693827100"
    private var errorPhoneNumber: String = "212"
    var activity: Activity = MainActivity()
    var code: String = "062629"
    var invalidCode: String = "2629"

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeDataUserManager(users)
        viewModel = PhoneAuthenticationViewModel(fakeManager)
    }

    @Test
    fun `Given a valid confirmation code it returns an empty String`() {
        val result = viewModel.validateConfirmationCode(code)
        assertEquals("", result)
    }

    @Test
    fun `Given a invalid confirmation code it returns an error String`() {
        val result = viewModel.validateConfirmationCode(invalidCode)
        assertNotNull(result)
    }

    @Test()
    fun `Given a inValid code will send a error string`() {
        runBlocking {
            val observer = Observer<String> {}
            try {
                viewModel.alertDialogErrorMessage.observeForever(observer)
                viewModel.verifyCode(invalidCode, activity)
                val value = viewModel.alertDialogErrorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.alertDialogErrorMessage.removeObserver(observer)
            }
        }
    }

    @Test
    fun `Given a valid phone number it returns an empty String`() {
        val result = viewModel.validatePhoneNumber(phoneNumber)
        assertEquals("", result)
    }

    @Test
    fun `Given a valid phone number with less then 4 digits it returns an error string`() {
        val result = viewModel.validatePhoneNumber(errorPhoneNumber)
        assertNotNull(result)
    }

    @Test()
    fun `Given a inValid phone number will send a error string`() {
        runBlocking {
            val observer = Observer<String> {}
            try {
                viewModel.errorMessage.observeForever(observer)
                viewModel.sendVerificationCode(code, errorPhoneNumber, activity)
                val value = viewModel.errorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.errorMessage.removeObserver(observer)
            }
        }
    }
}
