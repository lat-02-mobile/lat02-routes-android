package com.jalasoft.routesapp.ui.cityPicker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.databinding.FragmentCityPickerBinding
import com.jalasoft.routesapp.ui.cityPicker.adapters.CityAdapter
import com.jalasoft.routesapp.ui.cityPicker.viewModel.CityPickerViewModel
import com.jalasoft.routesapp.ui.settings.viewModel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityPickerFragment : Fragment(), CityAdapter.ICityListener {

    private var _binding: FragmentCityPickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CityPickerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()

        viewModel.citiesList.observe(viewLifecycleOwner) {
            (binding.cityPickerRecycler.adapter as CityAdapter).updateList(it.toMutableList())
        }

        viewModel.fetchCities()
    }

    private fun setRecycler() {
        binding.cityPickerRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.cityPickerRecycler.adapter = CityAdapter(mutableListOf(), this)
    }

    override fun onCountryTap(city: City) {
    }

}