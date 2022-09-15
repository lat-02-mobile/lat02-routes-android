package com.jalasoft.routesapp.ui.cityPicker.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.databinding.FragmentSplashScreenBinding
import com.jalasoft.routesapp.ui.cityPicker.viewModel.SplashScreenViewModel
import com.jalasoft.routesapp.util.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private val args: SplashScreenFragmentArgs by navArgs()
    private val viewModel: SplashScreenViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.welcomeMessageCity.text = args.city
        val text = RoutesAppApplication.resource?.getString(R.string.loading_data)
        progressDialog.start(text!!)

        viewModel.getDataAndSafeLocally(requireContext())
        viewModel.dataSaved.observe(viewLifecycleOwner) {
            if (it) {
                progressDialog.stop()
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            }
        }
    }
}
