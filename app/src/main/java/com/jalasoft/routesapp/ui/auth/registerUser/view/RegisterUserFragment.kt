package com.jalasoft.routesapp.ui.auth.registerUser.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.firebase.FirebaseApp
import com.jalasoft.routesapp.databinding.FragmentRegisterUserBinding
import com.jalasoft.routesapp.ui.auth.registerUser.viewModel.RegisterUserViewModel

class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterUserViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        FirebaseApp.initializeApp(context)
        viewModel.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding.btnRegRegister.setOnClickListener {
            addUser()
        }
    }

    private fun addUser() {
        showProgress(true)
        val name = binding.etRegName.text.toString()
        val email = binding.etRegEmail.text.toString()
        val password = binding.etRegPassword.text.toString()
        val confirmPassword = binding.etRegConfirmPassword.text.toString()

        viewModel.registerUserAuth(name, email, password, confirmPassword) {
            if (viewModel.registerUser) {
                binding.txtRegError.isVisible = false
                binding.txtRegError.setText("")
                binding.etRegName.setText("")
                binding.etRegEmail.setText("")
                binding.etRegPassword.setText("")
                binding.etRegConfirmPassword.setText("")
                showProgress(false)
                Toast.makeText(context, "Register Successfully", Toast.LENGTH_SHORT).show()
            } else {
                showProgress(false)
                binding.txtRegError.isVisible = true
                binding.txtRegError.setText(viewModel.errorMessage)
            }
        }
    }

    fun showProgress(show: Boolean) {
        if (show) {
            binding.pbRegUser.visibility = View.VISIBLE
        } else {
            binding.pbRegUser.visibility = View.GONE
        }
    }
}
