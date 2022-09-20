package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import androidx.lifecycle.Observer
import com.google.firebase.auth.GoogleAuthProvider
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.source.FakeDataUserManager
import com.jalasoft.routesapp.data.source.getOrAwaitValue
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, manifest = Config.NONE)
@HiltAndroidTest
class RegisterUserViewModelTest : TestCase() {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Mock
    var credential = GoogleAuthProvider.getCredential("eyJhbGciOiJSUzI1NiIsImtpZCI6ImZkYTEwNjY0NTNkYzlkYzNkZDkzM2E0MWVhNTdkYTNlZjI0MmIwZjciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMTA0MjM5NjkwOC1sdWdsc2ZmMTk5ODFlY2ZuOHFwYnBuYWFuZTJibHBzdC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjExMDQyMzk2OTA4LWM0dGY0Y21mZW4yOWU2b2xuN3V1bTEzNHIzdnN2bzA4LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTEwODI0NzA0MTU1NDM2MTgzOTU0IiwiZW1haWwiOiJyb2RyaWdvLnNjaGFyQGphbGEtZm91bmRhdGlvbi5vcmciLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlJvZHJpZ28gU2NoYXIgQW50ZWxvIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FJdGJ2bWsyTUloOVFFS252WGx6aXpHYzZ5NmhIcFJJRXI4NVBjTUdCS2QwPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IlJvZHJpZ28iLCJmYW1pbHlfbmFtZSI6IlNjaGFyIEFudGVsbyIsImxvY2FsZSI6ImVzIiwiaWF0IjoxNjYwNTk5NjI5LCJleHAiOjE2NjA2MDMyMjl9.K5kBmOiglc48oYb8a9mAjbEzOyA1-pwCn4zi9sUgyTdnXcg3YVuEEtjX_75fdZm1tMjwMmgoVG6Lkm23UJ4WIRu8F6NI1YM9C_sn-uVJsy18CL66IWki_BArP_zlt-0NBcFkG1Y9UgTkLx0dQByQRaSPpwqQbohVP8FByHV3JPdbLYDSBVDVQr392uK7y-ElTkkx2yhGsD3Q_T3K2eElfY8GIFN2qfLIxghwbNuD9KGJsetSB8E1_Q4_ttMZOjDdJlLhKsUR7rt2GXfbhh5lYmUTQ0SqojHJErnwe0Sj_fvyZvcg9GWT1FmxVmAm-nDccjBmXijvlCHX3_OsG5_pxw", null)

    private lateinit var viewModel: RegisterUserViewModel
    private lateinit var fakeManager: FakeDataUserManager

    private var uid: String = "AS2a2w2asaw21s"
    private var names: String = "test"
    private var email: String = "test@gmail.com"
    private var password: String = "123456"
    private var typeLogin: UserTypeLogin = UserTypeLogin.GOOGLE
    private var confirmPassword: String = "123456"
    private var users: MutableList<User> = mutableListOf(User())
    private var user = User("asd", names, email, "0", 0, typeLogin.int, 1.0, 1.0)

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeDataUserManager(users)
        viewModel = RegisterUserViewModel(fakeManager)
    }

    @Test
    fun `Given valid and not empty fields it returns an empty String`() {
        val result = viewModel.validateFields(names, email, password, confirmPassword)
        assertEquals("", result)
    }

    @Test
    fun `Given empty fields or invalid fields it returns an error String`() {
        val result = viewModel.validateFields("", email, password, confirmPassword)
        assertNotNull(result)
    }

    @Test
    fun `Given a Valid User and Email the User is Created in UserAuth and in the Database`() {
        runBlocking {
            viewModel.verifyRegisterUserAuth(names, email, password, confirmPassword)
            val resultAuth = fakeManager.createUserAuth(email, password)
            val result = fakeManager.createUser(uid, names, email, typeLogin)

            assertEquals(resultAuth.data.toString(), email)
            assertEquals(result.data.toString(), names)
        }
    }

    @Test
    fun `Given a Invalid Email it sets the error message to a liveData errorMessage variable`() {
        users.add(user)
        fakeManager = FakeDataUserManager(users)
        viewModel = RegisterUserViewModel(fakeManager)
        val observer = Observer<String> {}

        try {
            viewModel.errorMessage.observeForever(observer)
            viewModel.verifyRegisterUserAuth(names, email, password, confirmPassword)

            val value = viewModel.errorMessage.getOrAwaitValue()
            assertNotNull(value)
        } finally {
            viewModel.errorMessage.removeObserver(observer)
        }
    }

    @Test
    fun `Given Email it verifies if is valid it register the UserAuth and The User in the database`() {
        runBlocking {
            viewModel.registerUserAuth(names, email, password, true)
            val resultAuth = fakeManager.createUserAuth(email, password)
            val result = fakeManager.createUser(uid, names, email, typeLogin)

            assertEquals(resultAuth.data.toString(), email)
            assertEquals(result.data.toString(), names)
        }
    }

    @Test
    fun `Given Email it verifies if is not valid it sets the error message to a liveData errorMessage variable`() {
        val observer = Observer<String> {}

        try {
            viewModel.errorMessage.observeForever(observer)
            viewModel.registerUserAuth(names, email, password, false)

            val value = viewModel.errorMessage.getOrAwaitValue()
            assertNotNull(value)
        } finally {
            viewModel.errorMessage.removeObserver(observer)
        }
    }

    @Test
    fun `Given a valid User and is registered in UserAuth it registers the User in the database`() {
        runBlocking {
            val observer = Observer<Boolean> {}

            try {
                viewModel.registerUser.observeForever(observer)
                viewModel.registerUser(uid, names, email, UserTypeLogin.NORMAL)
                val result = fakeManager.createUser(uid, names, email, typeLogin)

                assertEquals(result.data.toString(), names)

                val value = viewModel.registerUser.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.registerUser.removeObserver(observer)
            }
        }
    }

    @Test
    fun `Given Email is verified and it is registered it sets the error message to a liveData errorMessage variable`() {
        users.add(user)
        fakeManager = FakeDataUserManager(users)
        viewModel = RegisterUserViewModel(fakeManager)
        val observer = Observer<String> {}

        try {
            viewModel.errorMessage.observeForever(observer)
            viewModel.validateEmailNormal(names, email, password)

            val value = viewModel.errorMessage.getOrAwaitValue()
            assertNotNull(value)
        } finally {
            viewModel.errorMessage.removeObserver(observer)
        }
    }
}
