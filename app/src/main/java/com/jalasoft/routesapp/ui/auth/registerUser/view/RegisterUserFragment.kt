package com.jalasoft.routesapp.ui.auth.registerUser.view

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.databinding.FragmentRegisterUserBinding
import com.jalasoft.routesapp.ui.auth.registerUser.viewModel.RegisterUserViewModel

class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterUserViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        FirebaseApp.initializeApp(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        if (validateFields()) {
            showProgress(true)
            val name = binding.etRegName.text.toString()
            val email = binding.etRegEmail.text.toString()
            val password = binding.etRegPassword.text.toString()

            viewModel.registerUserAuth(name, email, password, { _ ->
                binding.txtRegError.isVisible = false
                binding.txtRegError.setText("")
                binding.etRegName.setText("")
                binding.etRegEmail.setText("")
                binding.etRegPassword.setText("")
                binding.etRegConfirmPassword.setText("")
                showProgress(false)
                Toast.makeText(context,"Register Successfully",Toast.LENGTH_SHORT).show()
            }, { error ->
                showProgress(false)
                binding.txtRegError.isVisible = true
                binding.txtRegError.setText(error)
            })
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val name = binding.etRegName.text.toString()
        val email = binding.etRegEmail.text.toString()
        val password = binding.etRegPassword.text.toString()
        val confirmPassword = binding.etRegConfirmPassword.text.toString()

        if (name.isEmpty()) {
            isValid = false
            binding.txtRegError.isVisible = true
            binding.txtRegError.setText(context?.getString(R.string.reg_val_name))
            return isValid
        }
        if (email.isEmpty()) {
            isValid = false
            binding.txtRegError.isVisible = true
            binding.txtRegError.setText(context?.getString(R.string.reg_val_email))
            return isValid
        }
        if (password.isEmpty()) {
            isValid = false
            binding.txtRegError.isVisible = true
            binding.txtRegError.setText(context?.getString(R.string.reg_val_password))
            return isValid
        }
        if (confirmPassword.isEmpty()) {
            isValid = false
            binding.txtRegError.isVisible = true
            binding.txtRegError.setText(context?.getString(R.string.reg_val_confirm_password))
            return isValid
        }
        if (password != confirmPassword) {
            isValid = false
            binding.txtRegError.isVisible = true
            binding.txtRegError.setText(context?.getString(R.string.reg_val_incorrect_passwords))
            return isValid
        }
        return isValid
    }

    fun showProgress(show:Boolean){
        if (show){
            binding.pbRegUser.visibility = View.VISIBLE
        }else {
            binding.pbRegUser.visibility = View.GONE
        }

    }
}