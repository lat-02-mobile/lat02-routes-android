package com.jalasoft.routesapp.ui.cityPicker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.databinding.FragmentCityPickerBinding
import com.jalasoft.routesapp.ui.cityPicker.adapters.CityAdapter
import com.jalasoft.routesapp.ui.cityPicker.viewModel.CityPickerViewModel
import com.jalasoft.routesapp.util.CustomProgressDialog
import com.jalasoft.routesapp.util.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityPickerFragment : Fragment(), CityAdapter.ICityListener {

    private var _binding: FragmentCityPickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CityPickerViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()

        binding.svCitySearcher.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterCities(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterCities(newText.toString())
                return true
            }
        })

        val currentCity = PreferenceManager.getCurrentCity(requireContext())
        if (currentCity == PreferenceManager.NOT_FOUND) {
            binding.btnBack.visibility = View.GONE
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.citiesList.observe(viewLifecycleOwner) {
            (binding.cityPickerRecycler.adapter as CityAdapter).updateList(it.toMutableList())
        }

        viewModel.fetchCities()
    }

    private fun setRecycler() {
        binding.cityPickerRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.cityPickerRecycler.adapter = CityAdapter(mutableListOf(), this)
    }

    override fun onCityTap(city: City) {
        val text = RoutesAppApplication.resource?.getString(R.string.loading_data)
        progressDialog.start(text!!)
        PreferenceManager.saveCurrentLocation(requireContext(), city.lat, city.lng, city.name, city.id)
        viewModel.getTourPointsLocal(requireContext())
        viewModel.dataSaved.observe(viewLifecycleOwner) {
            if (it) {
                progressDialog.stop()
                val direction = CityPickerFragmentDirections.actionCityPickerFragmentToSplashScreenFragment(city = city.name)
                findNavController().navigate(direction)
            }
        }
    }
}
