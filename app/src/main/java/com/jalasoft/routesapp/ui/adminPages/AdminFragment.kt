package com.jalasoft.routesapp.ui.adminPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLinesAdmin.setOnClickListener {
            findNavController().navigate(R.id.linesAdminFragment)
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnUserPromote.setOnClickListener {
            findNavController().navigate(R.id.promoteUsersFragment)
        }

        binding.btnCities.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_citiesFragment)
        }
    }
}
