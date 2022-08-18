package com.jalasoft.routesapp.ui.auth.login.viewModel

import androidx.lifecycle.Observer
import com.google.firebase.auth.GoogleAuthProvider
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.source.FakeDataUserManager
import com.jalasoft.routesapp.data.source.getOrAwaitValue
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
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
class LoginViewModelTest : TestCase() {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Mock
    var credential = GoogleAuthProvider.getCredential("eyJhbGciOiJSUzI1NiIsImtpZCI6ImZkYTEwNjY0NTNkYzlkYzNkZDkzM2E0MWVhNTdkYTNlZjI0MmIwZjciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMTA0MjM5NjkwOC1sdWdsc2ZmMTk5ODFlY2ZuOHFwYnBuYWFuZTJibHBzdC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjExMDQyMzk2OTA4LWM0dGY0Y21mZW4yOWU2b2xuN3V1bTEzNHIzdnN2bzA4LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTEwODI0NzA0MTU1NDM2MTgzOTU0IiwiZW1haWwiOiJyb2RyaWdvLnNjaGFyQGphbGEtZm91bmRhdGlvbi5vcmciLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlJvZHJpZ28gU2NoYXIgQW50ZWxvIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FJdGJ2bWsyTUloOVFFS252WGx6aXpHYzZ5NmhIcFJJRXI4NVBjTUdCS2QwPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IlJvZHJpZ28iLCJmYW1pbHlfbmFtZSI6IlNjaGFyIEFudGVsbyIsImxvY2FsZSI6ImVzIiwiaWF0IjoxNjYwNTk5NjI5LCJleHAiOjE2NjA2MDMyMjl9.K5kBmOiglc48oYb8a9mAjbEzOyA1-pwCn4zi9sUgyTdnXcg3YVuEEtjX_75fdZm1tMjwMmgoVG6Lkm23UJ4WIRu8F6NI1YM9C_sn-uVJsy18CL66IWki_BArP_zlt-0NBcFkG1Y9UgTkLx0dQByQRaSPpwqQbohVP8FByHV3JPdbLYDSBVDVQr392uK7y-ElTkkx2yhGsD3Q_T3K2eElfY8GIFN2qfLIxghwbNuD9KGJsetSB8E1_Q4_ttMZOjDdJlLhKsUR7rt2GXfbhh5lYmUTQ0SqojHJErnwe0Sj_fvyZvcg9GWT1FmxVmAm-nDccjBmXijvlCHX3_OsG5_pxw", null)

    lateinit var viewModel: LoginViewModel
    lateinit var fakeManager: FakeDataUserManager

    var names: String = "test"
    var email: String = "test@gmail.com"
    val typeLogin: UserTypeLogin = UserTypeLogin.GOOGLE
    var users: MutableList<User> = mutableListOf()
    var user = User("asd", names, email, "0", 0, typeLogin.int, 1.0, 1.0)

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeDataUserManager(users)
        viewModel = LoginViewModel(fakeManager)
    }

    @Test()
    fun `Given a valid Google Account in case the User is not registered it register in the database and SignIn`() {
        runBlocking {
            viewModel.validateEmailGoogle(names, email, typeLogin, credential)
            val result = fakeManager.createUser(names, email, typeLogin)
            assertEquals(result.data.toString(), names)

            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogle.observeForever(observer)
                val value = viewModel.signInGoogle.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogle.removeObserver(observer)
            }
        }
    }

    @Test()
    fun `Given a valid Google Account in case the User is registered it only SignIn`() {
        users.add(user)
        fakeManager = FakeDataUserManager(users)
        viewModel = LoginViewModel(fakeManager)

        runBlocking {
            viewModel.validateEmailGoogle(names, email, typeLogin, credential)

            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogle.observeForever(observer)
                val value = viewModel.signInGoogle.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogle.removeObserver(observer)
            }
        }
    }

    @Test()
    fun `Given Google Email is verify in case the Email is not register it register the User and SignIn`() {
        runBlocking {
            viewModel.userGoogleAuth(names, email, typeLogin, credential, true)
            val result = fakeManager.createUser(names, email, typeLogin)
            assertEquals(result.data.toString(), names)

            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogle.observeForever(observer)
                val value = viewModel.signInGoogle.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogle.removeObserver(observer)
            }
        }
    }

    @Test()
    fun `Given Google Email is verify in case the Email is registered it SignIn`() {
        runBlocking {
            viewModel.userGoogleAuth(names, email, typeLogin, credential, false)

            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogle.observeForever(observer)
                val value = viewModel.signInGoogle.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogle.removeObserver(observer)
            }
        }
    }

    @Test()
    fun `Given Google Email is not register it register the User and SignIn`() {
        runBlocking {
            viewModel.registerUserWithGoogle(names, email, typeLogin, credential)
            val result = fakeManager.createUser(names, email, typeLogin)
            assertEquals(result.data.toString(), names)

            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogle.observeForever(observer)
                val value = viewModel.signInGoogle.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogle.removeObserver(observer)
            }
        }
    }

    @Test()
    fun `Given Google Email is not register and it register the user or Email is registered it SignIn`() {
        runBlocking {
            viewModel.singInWithGoogleCredentials(credential)

            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogle.observeForever(observer)
                val value = viewModel.signInGoogle.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogle.removeObserver(observer)
            }
        }
    }
}
