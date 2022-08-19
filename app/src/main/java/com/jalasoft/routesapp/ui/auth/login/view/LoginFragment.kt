package com.jalasoft.routesapp.ui.auth.login.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.databinding.FragmentLoginBinding
import com.jalasoft.routesapp.ui.auth.login.viewModel.LoginViewModel
import com.jalasoft.routesapp.util.FacebookGoogleAuthUtil
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var googleSingInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var fbLoginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callbackManager = CallbackManager.Factory.create()
        fbLoginManager = LoginManager.getInstance()

        googleSingInClient = FacebookGoogleAuthUtil.googleConfiguration(binding.root.context)

        FacebookGoogleAuthUtil.facebookConfiguration(callbackManager, fbLoginManager) { displayName, email, userTypeLogin, credential ->
            updateUI(displayName, email, userTypeLogin, credential)
        }

        buttonActions()

        viewModel.context = context
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun buttonActions() {
        binding.btnLogRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerUserFragment)
        }

        binding.btnLogin.setOnClickListener {
            loginWithEmailAndPassword()
        }

        binding.ibLogGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.ibLogFacebook.setOnClickListener {
            fbLoginManager.logIn(this, FacebookGoogleAuthUtil.FB_PERMISSIONS)
        }
    }

    private fun loginWithEmailAndPassword() {
        showProgress(true)
        val email = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()

        viewModel.loginUserAuth(email, password)
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            binding.txtError.isVisible = true
            binding.txtError.text = errorMessage
            showProgress(false)
        }
        val resultObserver = Observer<Boolean> {
            showProgress(false)
            binding.loginEmail.setText("")
            binding.loginPassword.setText("")
            findNavController().navigate(R.id.homeFragment)
            Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
        }
        val googleAndFacebookObserver = Observer<Boolean> { value ->
            if (value) {
                findNavController().navigate(R.id.homeFragment)
                showProgress(false)
            }
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.loginIsSuccessful.observe(this, resultObserver)
        viewModel.signInGoogleOrFacebook.observe(this, googleAndFacebookObserver)
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.pbLogin.visibility = View.VISIBLE
        } else {
            binding.pbLogin.visibility = View.GONE
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSingInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            FacebookGoogleAuthUtil.handleGoogleResults(task) { displayName, email, userTypeLogin, credential ->
                updateUI(displayName, email, userTypeLogin, credential)
            }
        }
    }

    private fun updateUI(displayName: String, email: String, userTypeLogin: UserTypeLogin, credential: AuthCredential) {
        showProgress(true)
        viewModel.validateEmailGoogleOrFacebook(displayName, email, userTypeLogin, credential)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
