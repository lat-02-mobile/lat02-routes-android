package com.jalasoft.routesapp.util

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import org.json.JSONException

object FacebookGoogleAuthUtil {

    val FB_PERMISSIONS = listOf("email", "public_profile")
    private const val FB_EMAIL = "email"
    private const val FB_NAME = "name"

    fun googleConfiguration(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun facebookConfiguration(callbackManager: CallbackManager, fbLoginManager: LoginManager, onCompleteListener: (displayName: String, email: String, userTypeLogin: UserTypeLogin, credential: AuthCredential) -> Unit) {
        fbLoginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    handleFacebookAccessToken(result.accessToken) { name, email ->
                        onCompleteListener(name, email, UserTypeLogin.FACEBOOK, credential)
                    }
                }
                override fun onCancel() {
                    Log.d("Facebook", "facebook:onCancel")
                }
                override fun onError(error: FacebookException) {
                    Log.d("Facebook", "facebook:onError", error)
                }
            }
        )
    }

    fun handleGoogleResults(task: Task<GoogleSignInAccount>, onCompleteListener: (displayName: String, email: String, userTypeLogin: UserTypeLogin, credential: AuthCredential) -> Unit) {
        val account: GoogleSignInAccount? = task.result
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            onCompleteListener(account.displayName.toString(), account.email.toString(), UserTypeLogin.GOOGLE, credential)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken, onCompleteListener: (displayName: String, email: String) -> Unit) {
        val request = GraphRequest.newMeRequest(token) { obj, _ ->
            if (obj != null) {
                try {
                    val name = obj.getString(FB_NAME)
                    val email = obj.getString(FB_EMAIL)
                    onCompleteListener(name, email)
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "$FB_NAME, $FB_EMAIL")
        request.parameters = parameters
        request.executeAsync()
    }
}
