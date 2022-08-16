package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class RegisterUserViewModelTest : TestCase() {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: RegisterUserViewModel
    var names: String = "test"
    var email: String = "test@gmail.com"
    var password: String = "123456"
    var confirmPassword: String = "123456"

    @BeforeEach
    public override fun setUp() {
        super.setUp()
        // viewModel = RegisterUserViewModel()
    }

    @Test
    fun testForValidateFieldsSuccess() {
        // val result = viewModel.validateFields(names, email, password, confirmPassword)
        // assertEquals("", result)
    }

    @Test
    fun testForValidateFieldsFailure() {
        // val result = viewModel.validateFields("", email, password, confirmPassword)
        // assertNotNull(result)
    }
}
