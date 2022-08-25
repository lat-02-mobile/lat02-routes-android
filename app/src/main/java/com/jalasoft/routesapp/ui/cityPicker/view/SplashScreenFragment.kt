package com.jalasoft.routesapp.ui.cityPicker.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private val args: SplashScreenFragmentArgs by navArgs()

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

        lifecycleScope.launch {
            delay(3000L)
            withContext(Dispatchers.Main) {
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            }
        }
    }
}
