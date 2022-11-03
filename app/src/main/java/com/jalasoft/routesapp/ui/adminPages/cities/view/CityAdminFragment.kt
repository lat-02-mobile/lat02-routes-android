package com.jalasoft.routesapp.ui.adminPages.cities.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.databinding.FragmentCitiesAdminBinding
import com.jalasoft.routesapp.ui.adminPages.cities.adapter.CityAdminAdapter
import com.jalasoft.routesapp.ui.adminPages.cities.viewModel.CityAdminViewModel
import com.jalasoft.routesapp.util.SwipeGesture
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityAdminFragment : Fragment(), CityAdminAdapter.ICityAdminListener {

    private var _binding: FragmentCitiesAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CityAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCitiesAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()

        binding.progressBar.visibility = View.VISIBLE
        viewModel.cityList.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            (binding.recyclerCitiesAdministrator.adapter as CityAdminAdapter).updateList(it.toMutableList())
        }

        viewModel.fetchCities()

        binding.lapSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchQuery = query.toString()
                viewModel.applyFilterAndSort()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchQuery = newText.toString()
                viewModel.applyFilterAndSort()
                return true
            }
        })

        binding.lapTopAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddNewCity.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.BUNDLE_KEY_CITY_ADMIN_IS_NEW, true)
            findNavController().navigate(R.id.cityAdminDetailFragment, bundle)
        }

        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                binding.recyclerCitiesAdministrator.adapter?.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                val setCity = (binding.recyclerCitiesAdministrator.adapter as CityAdminAdapter).citiesList[viewHolder.absoluteAdapterPosition]
                val position = (binding.recyclerCitiesAdministrator.adapter as CityAdminAdapter).citiesList.indexOf(setCity)
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val builder = AlertDialog.Builder(binding.root.context)
                        builder.setTitle(R.string.remove_city)
                        builder.setMessage(requireContext().getString(R.string.sure_remove_line, setCity.name))
                        builder.setPositiveButton(R.string.yes) { _, _ ->
                            viewModel.deleteCity(setCity.id)
                            binding.recyclerCitiesAdministrator.adapter?.notifyItemRemoved(position)
                        }
                        builder.setNegativeButton(R.string.cancel) { _, _ ->
                        }
                        builder.show()
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.recyclerCitiesAdministrator)
    }

    private fun setRecycler() {
        binding.recyclerCitiesAdministrator.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCitiesAdministrator.adapter = CityAdminAdapter(mutableListOf(), this)
    }

    override fun gotoEditCity(city: City) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.BUNDLE_KEY_CITY_ADMIN_SELECTED_DATA, city)
        bundle.putBoolean(Constants.BUNDLE_KEY_CITY_ADMIN_IS_NEW, false)
        findNavController().navigate(R.id.cityAdminDetailFragment, bundle)
    }
}
