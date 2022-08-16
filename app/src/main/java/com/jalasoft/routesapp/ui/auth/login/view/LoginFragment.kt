package com.jalasoft.routesapp.ui.auth.login.view

import android.app.Activity
import android.content.Context
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.GoogleAuthProvider
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.databinding.FragmentLoginBinding
import com.jalasoft.routesapp.ui.auth.login.viewModel.LoginViewModel
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var googleSingInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        FirebaseApp.initializeApp(context)
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
        googleConfiguration()
        viewModel.context = context
        binding.btnLogRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerUserFragment)
        }

        binding.btnLogin.setOnClickListener {
            loginWithEmailAndPassword()
        }

        binding.ibLogGoogle.setOnClickListener {
            signInGoogle()
        }
    }

    private fun googleConfiguration() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_cliente_id))
            .requestEmail()
            .build()
        googleSingInClient = GoogleSignIn.getClient(binding.root.context, gso)
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
        val googleObserver = Observer<Boolean> { value ->
            if (value) {
                findNavController().navigate(R.id.homeFragment)
                showProgress(false)
            }
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.loginIsSuccessful.observe(this, resultObserver)
        viewModel.signInGoogle.observe(this, googleObserver)
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.pbLogin.visibility = View.VISIBLE
        } else {
            binding.pbLogin.visibility = View.GONE
        }
    }

    /*fun goToHomeFragment(){
        if(loginIsSuccessful == true){
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }
*/
    /*companion object {
        const val tv_email = "email"
        const val providerType = "provider"

        fun newInstance(email: String, provider: LoginViewModel.ProviderType): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(tv_email, email)
            args.putString(providerType, provider.name)
            fragment.arguments = args
            return fragment
        }
    }*/

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
}
