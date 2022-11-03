package com.jalasoft.routesapp.ui.adminPages.cities.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.databinding.FragmentCitiesAdminDetailBinding
import com.jalasoft.routesapp.ui.adminPages.cities.adapter.SpinnerCountryAdapter
import com.jalasoft.routesapp.ui.adminPages.cities.viewModel.CityAdminViewModel
import com.jalasoft.routesapp.util.Extensions.toEditable
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityAdminDetailFragment : Fragment() {

    private var _binding: FragmentCitiesAdminDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CityAdminViewModel by viewModels()
    private var city: City? = null  // line: LineAux
    private var isNew: Boolean = true
    private var editCityList: List<Country> = listOf()  // private var editCategoryList: List<City> = listOf()
    // private var editCityList: List<City> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCitiesAdminDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isNew = arguments?.getSerializable(Constants.BUNDLE_KEY_CITY_ADMIN_IS_NEW) as Boolean
        if (isNew) {
            binding.btnEditSave.text = RoutesAppApplication.resource?.getString(R.string.city_btn_save).toString()
        } else {
            city = arguments?.getSerializable(Constants.BUNDLE_KEY_CITY_ADMIN_SELECTED_DATA) as City
            loadData(city)
            binding.btnEditSave.text = RoutesAppApplication.resource?.getString(R.string.btn_edit_city).toString()
        }

        viewModel.cityCountry.observe(viewLifecycleOwner) {
            setCountrySpinner(it.toMutableList())
        }

        viewModel.getCityCountry()
        buttonActions()
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle(R.string.field_empty)
            builder.setMessage(errorMessage)
            builder.setPositiveButton(R.string.ok) { _, _ ->
            }
            builder.show()
            showProgress(false)
        }
        val successResult = Observer<Boolean> { successResult ->
            if (successResult) {
                val message = if (isNew) RoutesAppApplication.resource?.getString(R.string.register_success).toString() else RoutesAppApplication.resource?.getString(R.string.edited_success).toString()
                showProgress(false)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                showProgress(false)
            }
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.successResult.observe(this, successResult)
    }

    private fun loadData(city: City?) {
        val name = city?.name ?: ""
        binding.etName.text = name.toEditable()
        binding.etLat.text=city?.lat?.toEditable()
        binding.etLng.text=city?.lat?.toEditable()
    }

    private fun buttonActions() {
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isNew) {
                    val item = viewModel.cityCountry.value?.get(position)
                    viewModel.countrySelected = item?.name ?: ""
                    viewModel.countryID = item?.id?:""
                } else {
                    val item = editCityList[position]
                    viewModel.countrySelected = item.name
                    viewModel.countryID = item?.id?:""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (isNew) {
                    viewModel.countrySelected = ""
                } else {
                    viewModel.countrySelected = city?.idCountry ?: ""
                }
            }
        }

        binding.btnEditSave.setOnClickListener {
            if (isNew) {
                showProgress(true)
                val name = binding.etName.text.toString()
                val lat = binding.etLat.text.toString()
                val lng = binding.etLng.text.toString()
                val selectedCountry = (binding.spCategory.selectedItem as Country).name
                val countyId =viewModel.cityCountry?.value?.get(binding.spCategory.selectedItemPosition)?.id
                viewModel.saveNewCity(name,lat,lng,selectedCountry,countyId?:"0")
            } else {
                showProgress(true)
                val name = binding.etName.text.toString()
                val lat = binding.etLat.text.toString()
                val lng = binding.etLng.text.toString()
                val selectedCountry = (binding.spCategory.selectedItem as Country).name
                val countyId =viewModel.cityCountry?.value?.get(binding.spCategory.selectedItemPosition)?.id
                viewModel.updateCity(name,lat,lng,selectedCountry,countyId?:"0",city!!)
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setCountrySpinner(list: List<Country>) {
        if (isNew) {
            val adapter = SpinnerCountryAdapter(requireContext(), list)
            binding.spCategory.adapter = adapter
        } else {
            val countryFiltered: List<Country> = list.filter { it.code == city?.idCountry }
            val listFiltered: List<Country> = list.filterNot { it.code == city?.idCountry }
            var joinList: ArrayList<Country> = arrayListOf()
            joinList.addAll(0, countryFiltered)
            joinList.addAll(listFiltered)
            val adapter = SpinnerCountryAdapter(requireContext(), joinList)
            binding.spCategory.adapter = adapter
            editCityList = joinList
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.pbRegUser.visibility = View.VISIBLE
        } else {
            binding.pbRegUser.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
