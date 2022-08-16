package com.jalasoft.routesapp.ui.auth.login.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.databinding.FragmentLoginBinding
import com.jalasoft.routesapp.ui.auth.login.viewModel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        FirebaseApp.initializeApp(context)
        viewModel.context = context
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

        binding.btnLogRegister.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerUserFragment)
        }

        binding.btnLogin.setOnClickListener{
            loginWithEmailAndPassword()
        }
    }

    private fun loginWithEmailAndPassword(){
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
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.loginIsSuccessful.observe(this, resultObserver)
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

}
