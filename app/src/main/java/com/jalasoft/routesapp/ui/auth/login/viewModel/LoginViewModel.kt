package com.jalasoft.routesapp.ui.auth.login.viewModel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.remote.managers.AuthFirebaseManager
import com.jalasoft.routesapp.data.remote.managers.UserManager
import com.jalasoft.routesapp.ui.auth.login.view.LoginFragment


class LoginViewModel : ViewModel() {

    val loginIsSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    @SuppressLint("StaticFieldLeak")
    var context: Context? = null

    /*fun loginUserAuth(email: String, password: String): Boolean {
        if (validateFields(email, password)) {
            AuthFirebaseManager.loginUserAuth(email, password, {
               // loginUser(email, password)
                //change to true
                loginIsSuccessful = true
            }, { error ->
                loginIs465564Successful = false
                errorMessage.value = error
            })
        }*/

    fun loginUserAuth(email: String, password: String) {
        val valid = validateFields(email, password)
        var user = FirebaseAuth.getInstance().currentUser
        if(user == null){
            if (valid.isEmpty()) {
                if (validateEmail(email)) {
                    AuthFirebaseManager.loginUserAuth(email, password, {
                        loginIsSuccessful.value = true
                        Log.d("sda", "succ")
                        print("success")
                    }, {
                        loginIsSuccessful.value = true
                        print("error")
                        Log.d("sda", "${it}estrellita")
                    })
                } else {
                    loginIsSuccessful.value = false
                    print("fail")
                }
            }
        }
    }

    private fun showErrorAlert(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Error authenticating user")
        builder.setPositiveButton("Ok", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun validateFields(email: String, password: String): String {
        var isValid = ""
        if (email.isEmpty() && password.isEmpty()) {
            isValid = context?.getString(R.string.reg_val_name).toString()
            errorMessage.value = "Empty Fields"
            showErrorAlert()
            return isValid
        }
        if(email.isEmpty() && password.isNotEmpty()){
            isValid = context?.getString(R.string.reg_val_email).toString()
            errorMessage.value = "Empty Email"
            showErrorAlert()
            return isValid
        }
        if(email.isNotEmpty() && password.isEmpty()){
            isValid = context?.getString(R.string.reg_val_password).toString()
            errorMessage.value = "Empty Password"
            showErrorAlert()
            return isValid
        }
        validateEmail(email)

        return isValid
    }

    private fun validateEmail(email: String): Boolean {
        var isValid = true
        UserManager.validateEmailUser(email, { users ->
            if (users.isNotEmpty()) {
                isValid = false
            }
        }, { error ->
            Log.d(ContentValues.TAG, error)
        })
        return isValid
    }

    /*
    fun permissionGranted(email: String, provider: LoginViewModel.ProviderType){
        fragmentTransaction.replace(com.jalasoft.routesapp.R.id.action_loginFragment_to_homeFragment, LoginFragment())
        putExtra()
        fragmentTransaction.commit()
    }*/

}





















