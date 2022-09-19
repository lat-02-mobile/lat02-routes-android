package com.jalasoft.routesapp.ui.auth

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
class AuthBaseViewModelTest : TestCase() {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Mock
    var credential = GoogleAuthProvider.getCredential("eyJhbGciOiJSUzI1NiIsImtpZCI6ImZkYTEwNjY0NTNkYzlkYzNkZDkzM2E0MWVhNTdkYTNlZjI0MmIwZjciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMTA0MjM5NjkwOC1sdWdsc2ZmMTk5ODFlY2ZuOHFwYnBuYWFuZTJibHBzdC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjExMDQyMzk2OTA4LWM0dGY0Y21mZW4yOWU2b2xuN3V1bTEzNHIzdnN2bzA4LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTEwODI0NzA0MTU1NDM2MTgzOTU0IiwiZW1haWwiOiJyb2RyaWdvLnNjaGFyQGphbGEtZm91bmRhdGlvbi5vcmciLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlJvZHJpZ28gU2NoYXIgQW50ZWxvIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FJdGJ2bWsyTUloOVFFS252WGx6aXpHYzZ5NmhIcFJJRXI4NVBjTUdCS2QwPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IlJvZHJpZ28iLCJmYW1pbHlfbmFtZSI6IlNjaGFyIEFudGVsbyIsImxvY2FsZSI6ImVzIiwiaWF0IjoxNjYwNTk5NjI5LCJleHAiOjE2NjA2MDMyMjl9.K5kBmOiglc48oYb8a9mAjbEzOyA1-pwCn4zi9sUgyTdnXcg3YVuEEtjX_75fdZm1tMjwMmgoVG6Lkm23UJ4WIRu8F6NI1YM9C_sn-uVJsy18CL66IWki_BArP_zlt-0NBcFkG1Y9UgTkLx0dQByQRaSPpwqQbohVP8FByHV3JPdbLYDSBVDVQr392uK7y-ElTkkx2yhGsD3Q_T3K2eElfY8GIFN2qfLIxghwbNuD9KGJsetSB8E1_Q4_ttMZOjDdJlLhKsUR7rt2GXfbhh5lYmUTQ0SqojHJErnwe0Sj_fvyZvcg9GWT1FmxVmAm-nDccjBmXijvlCHX3_OsG5_pxw", null)

    private lateinit var viewModel: AuthBaseViewModel
    private lateinit var fakeManager: FakeDataUserManager

    private var users: MutableList<User> = mutableListOf()
    private var uid: String = "AS2a2w2asaw21s"
    private var names: String = "test"
    private var email: String = "test@gmail.com"
    private var typeLogin: UserTypeLogin = UserTypeLogin.GOOGLE
    private var user = User("asd", names, email, "0", 0, typeLogin.int, 1.0, 1.0)

    @Before
    public override fun setUp() {
        super.setUp()
        hiltRule.inject()
        fakeManager = FakeDataUserManager(users)
        viewModel = AuthBaseViewModel(fakeManager)
    }

    @Test
    fun `Given a valid Google - Facebook Account in case the User is not registered it registers in the database and SignIn`() {
        runBlocking {
            viewModel.validateEmailGoogleOrFacebook(names, email, typeLogin, credential)
            val result = fakeManager.createUser(uid, names, email, typeLogin)
            assertEquals(result.data.toString(), names)
            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogleOrFacebook.observeForever(observer)
                val value = viewModel.signInGoogleOrFacebook.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogleOrFacebook.removeObserver(observer)
            }
        }
    }

    @Test
    fun `Given a valid Google - Facebook Account in case the User is registered and uses same provider it only SignIn`() {
        runBlocking {
            users.add(user)
            fakeManager = FakeDataUserManager(users)
            viewModel = AuthBaseViewModel(fakeManager)

            viewModel.validateEmailGoogleOrFacebook(names, email, typeLogin, credential)

            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogleOrFacebook.observeForever(observer)
                val value = viewModel.signInGoogleOrFacebook.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogleOrFacebook.removeObserver(observer)
            }
        }
    }

    @Test
    fun `Given a valid Google - Facebook Account in case the Email is registered with another provider it gives an error`() {
        runBlocking {
            users.add(user)
            fakeManager = FakeDataUserManager(users)
            viewModel = AuthBaseViewModel(fakeManager)

            viewModel.userAuthWithCredentials(names, email, UserTypeLogin.FACEBOOK, credential, user)
            val observer = Observer<String> {}
            try {
                viewModel.errorMessage.observeForever(observer)
                val value = viewModel.errorMessage.getOrAwaitValue()
                assertNotNull(value)
            } finally {
                viewModel.errorMessage.removeObserver(observer)
            }
        }
    }

    @Test
    fun `Given a valid credential from Google - Facebook Account it returns the user uid`() {
        runBlocking {
            viewModel.signInWithCredentials(credential)
            val observer = Observer<Boolean> {}
            try {
                viewModel.signInGoogleOrFacebook.observeForever(observer)
                val value = viewModel.signInGoogleOrFacebook.getOrAwaitValue()
                assertTrue(value)
            } finally {
                viewModel.signInGoogleOrFacebook.removeObserver(observer)
            }
        }
    }
}
