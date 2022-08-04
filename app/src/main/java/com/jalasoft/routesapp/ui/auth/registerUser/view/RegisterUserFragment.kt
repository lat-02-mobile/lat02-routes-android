package com.jalasoft.routesapp.ui.auth.registerUser.view

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.databinding.FragmentRegisterUserBinding
import com.jalasoft.routesapp.ui.auth.registerUser.viewModel.RegisterUserViewModel

class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!

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
        val name = binding.etRegName.text.toString()
        val email = binding.etRegEmail.text.toString()
        val password = binding.etRegPassword.text.toString()

        RegisterUserViewModel.registerUser(name, email, password)

        binding.etRegName.setText("")
        binding.etRegEmail.setText("")
        binding.etRegPassword.setText("")
    }
}