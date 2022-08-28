package com.jalasoft.routesapp.ui.auth.phoneAuthentication.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.remote.managers.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class PhoneAuthenticationViewModel
@Inject
constructor(private val repository: UserRepository) : ViewModel() {
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val alertDialogErrorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val codeSubmitted: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val onAutomaticVerification: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val onVerificationCompleted: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    private var phoneVerificationId: String = ""
    private lateinit var phoneCredential: PhoneAuthCredential

    fun sendVerificationCode(countryCode: String, phoneNumber: String, activity: Activity) = viewModelScope.launch {
        val valid = validatePhoneNumber(phoneNumber)
        if (valid.isEmpty()) {
            val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    phoneVerificationId = verificationId
                    codeSubmitted.value = true
                }
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    onAutomaticVerification.value = true
                    phoneCredential = credential
                }
                override fun onVerificationFailed(e: FirebaseException) {
                    errorMessage.value = e.toString()
                }
            }
            val result = repository.sendPhoneNumberCode(countryCode + phoneNumber, activity, mCallBack)
            if (result.message?.isNotEmpty() == true) errorMessage.value = result.message
        } else errorMessage.value = valid
    }

    fun verifyCode(code: String, activity: Activity) = viewModelScope.launch {
        val valid = validateConfirmationCode(code)
        if (valid.isEmpty()) {
            val phoneAuthCredential =
                if (code == "") phoneCredential else PhoneAuthProvider.getCredential(
                    phoneVerificationId,
                    code
                )
            val result = repository.verifyPhoneCode(phoneAuthCredential, activity)
            if (result.message?.isNotEmpty() == true) {
                alertDialogErrorMessage.value = result.message
            } else {
                onVerificationCompleted.value = true
            }
        } else {
            alertDialogErrorMessage.value = valid
        }
    }
    fun validatePhoneNumber(phoneNumber: String): String {
        var isValid = ""
        if (phoneNumber.isEmpty()) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_add_phone).toString()
            return isValid
        }
        if (phoneNumber.length < 4) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_verify_phone).toString()
            return isValid
        }
        return isValid
    }

    fun validateConfirmationCode(code: String): String {
        var isValid = ""
        if (code.isEmpty()) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_add_code).toString()
            return isValid
        }
        if (code.length < 6) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_verify_code).toString()
            return isValid
        }
        return isValid
    }
}
