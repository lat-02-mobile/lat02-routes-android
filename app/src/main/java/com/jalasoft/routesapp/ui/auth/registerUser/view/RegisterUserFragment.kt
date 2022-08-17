package com.jalasoft.routesapp.ui.auth.registerUser.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
import com.jalasoft.routesapp.databinding.FragmentRegisterUserBinding
import com.jalasoft.routesapp.ui.auth.registerUser.viewModel.RegisterUserViewModel
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException

@AndroidEntryPoint
class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterUserViewModel by viewModels()
    private lateinit var googleSingInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.context = context
        googleConfiguration()
        facebookConfiguration()
        buttonActions()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun buttonActions() {
        binding.btnRegRegister.setOnClickListener {
            addUser()
        }

        binding.ibRegGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.btnRegLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ibRegFacebook.setOnClickListener {
            loginManager.logIn(this, listOf("email", "public_profile"))
        }
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            binding.txtRegError.isVisible = true
            binding.txtRegError.text = errorMessage
            showProgress(false)
        }
        val resultObserver = Observer<Boolean> { value ->
            if (value) {
                showProgress(false)
                binding.txtRegError.isVisible = false
                binding.etRegName.setText("")
                binding.etRegEmail.setText("")
                binding.etRegPassword.setText("")
                binding.etRegConfirmPassword.setText("")
                findNavController().navigate(R.id.homeFragment)
            }
        }
        val googleObserver = Observer<Boolean> { value ->
            if (value) {
                showProgress(false)
                findNavController().navigate(R.id.homeFragment)
            }
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.registerUser.observe(this, resultObserver)
        viewModel.signInGoogleOrFacebook.observe(this, googleObserver)
    }

    private fun facebookConfiguration() {
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()
        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
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

    private fun googleConfiguration() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_cliente_id))
            .requestEmail()
            .build()
        googleSingInClient = GoogleSignIn.getClient(binding.root.context, gso)
    }

    private fun addUser() {
        showProgress(true)
        val name = binding.etRegName.text.toString()
        val email = binding.etRegEmail.text.toString()
        val password = binding.etRegPassword.text.toString()
        val confirmPassword = binding.etRegConfirmPassword.text.toString()

        viewModel.verifyRegisterUserAuth(name, email, password, confirmPassword)
    }

    private fun signInGoogle() {
        val signInIntent = googleSingInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleResults(task)
        }
    }

    private fun handleGoogleResults(task: Task<GoogleSignInAccount>) {
        val account: GoogleSignInAccount? = task.result
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            updateUI(account.displayName.toString(), account.email.toString(), UserTypeLogin.GOOGLE, credential)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        val request = GraphRequest.newMeRequest(token) { obj, _ ->
            if (obj != null) {
                try {
                    val name = obj.getString("name")
                    val email = obj.getString("email")
                    updateUI(name, email, UserTypeLogin.FACEBOOK, credential)
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "name, email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun updateUI(displayName: String, email: String, userTypeLogin: UserTypeLogin, credential: AuthCredential) {
        showProgress(true)
        viewModel.validateEmailGoogleOrFacebook(displayName, email, userTypeLogin, credential)
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.pbRegUser.visibility = View.VISIBLE
        } else {
            binding.pbRegUser.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
