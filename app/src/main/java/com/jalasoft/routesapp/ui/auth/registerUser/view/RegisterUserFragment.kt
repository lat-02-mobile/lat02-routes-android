package com.jalasoft.routesapp.ui.auth.registerUser.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.GoogleAuthProvider
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.databinding.FragmentRegisterUserBinding
import com.jalasoft.routesapp.ui.auth.registerUser.viewModel.RegisterUserViewModel
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterUserViewModel by viewModels()
    private lateinit var googleSingInClient: GoogleSignInClient

    override fun onAttach(context: Context) {
        super.onAttach(context)
        FirebaseApp.initializeApp(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.context = context
        googleConfiguration()
        buttonActions()
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
        viewModel.signInGoogle.observe(this, googleObserver)
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
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        val account: GoogleSignInAccount? = task.result
        if (account != null) {
            updateUI(account)
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        showProgress(true)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        viewModel.validateEmailGoogle(account.displayName.toString(), account.email.toString(), UserTypeLogin.GOOGLE, credential)
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
