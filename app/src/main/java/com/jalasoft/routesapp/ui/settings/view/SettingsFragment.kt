package com.jalasoft.routesapp.ui.settings.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amplitude.android.Amplitude
import com.jalasoft.routesapp.AuthActivity
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.managers.AuthFirebaseManager
import com.jalasoft.routesapp.databinding.FragmentSettingsBinding
import com.jalasoft.routesapp.ui.settings.viewModel.SettingsViewModel
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.UserType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var amplitude: Amplitude

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentCity = PreferenceManager.getCurrentCity(requireContext())
        amplitude = PreferenceManager.getAmplitude(binding.root.context.applicationContext)

        binding.btnSetCity.text = if (currentCity != PreferenceManager.NOT_FOUND) currentCity else getString(R.string.city_not_set_yet)

        binding.btnSetCity.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_cityPickerFragment)
        }

        binding.btnLogout.setOnClickListener {
            AuthFirebaseManager.signOutUser()
            PreferenceManager.deleteAllData(requireContext())
            amplitude.reset()
            val intent = Intent(activity, AuthActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }

        binding.btnAdmin.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_adminFragment)
        }

        binding.btnAdmin.isVisible = false
        viewModel.fetchUserDetails()
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }

        val userObserver = Observer<User> {
            it?.let { user ->
                user.type?.let { type ->
                    if (type == 0) {
                        adminMode(UserType.NORMAL)
                    } else {
                        adminMode(UserType.ADMIN)
                    }
                }
            }
        }

        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.user.observe(this, userObserver)
    }

    private fun adminMode(userType: UserType) {
        when (userType) {
            UserType.NORMAL -> {
                binding.btnAdmin.isVisible = false
            }
            UserType.ADMIN -> {
                binding.btnAdmin.isVisible = true
            }
        }
    }
}
