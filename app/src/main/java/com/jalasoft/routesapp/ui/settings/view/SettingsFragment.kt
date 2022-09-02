package com.jalasoft.routesapp.ui.settings.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jalasoft.routesapp.AuthActivity
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.remote.managers.AuthFirebaseManager
import com.jalasoft.routesapp.databinding.FragmentSettingsBinding
import com.jalasoft.routesapp.ui.settings.viewModel.SettingsViewModel
import com.jalasoft.routesapp.util.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

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

        binding.btnSetCity.text = if (currentCity != PreferenceManager.NOT_FOUND) currentCity else getString(R.string.city_not_set_yet)

        binding.btnSetCity.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_cityPickerFragment)
        }

        binding.btnLogout.setOnClickListener {
            AuthFirebaseManager.signOutUser()
            PreferenceManager.deleteAllData(requireContext())
            val intent = Intent(activity, AuthActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }
    }
}
