package com.jalasoft.routesapp.ui.auth.registerUser.view

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
import com.jalasoft.routesapp.MainActivity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.databinding.FragmentRegisterUserBinding
import com.jalasoft.routesapp.ui.auth.registerUser.viewModel.RegisterUserViewModel
import com.jalasoft.routesapp.util.FacebookGoogleAuthUtil
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterUserViewModel by viewModels()
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
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
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
            fbLoginManager.logIn(this, FacebookGoogleAuthUtil.FB_PERMISSIONS)
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
                findNavController().navigate(R.id.action_registerUserFragment_to_phoneAuthenticationFragment)
            }
        }
        val googleAndFacebookObserver = Observer<Boolean> { value ->
            if (value) {
                val phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
                if (phoneNumber == null) {
                    findNavController().navigate(R.id.action_loginFragment_to_phoneAuthenticationFragment)
                } else {
                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                    Toast.makeText(context, RoutesAppApplication.resource?.getString(R.string.login_success).toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.registerUser.observe(this, resultObserver)
        viewModel.signInGoogleOrFacebook.observe(this, googleAndFacebookObserver)
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
            FacebookGoogleAuthUtil.handleGoogleResults(task, googleSingInClient) { displayName, email, userTypeLogin, credential ->
                updateUI(displayName, email, userTypeLogin, credential)
            }
        }
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
