package com.jalasoft.routesapp.ui.tourPoints.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.databinding.FragmentTourPointsBinding
import com.jalasoft.routesapp.ui.tourPoints.adapter.TourPointsAdapter
import com.jalasoft.routesapp.ui.tourPoints.viewModel.TourPointsViewModel
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.FilterType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TourPointsFragment : Fragment(), TourPointsAdapter.ITourPointsListener {
    private var _binding: FragmentTourPointsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TourPointsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTourPointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()
        binding.progressBar.visibility = View.VISIBLE

        viewModel.tourPoints.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            (binding.recyclerTourPoints.adapter as TourPointsAdapter).updateList(it.toMutableList())
        }
        val currentCityId = PreferenceManager.getCurrentCityID(requireContext())
        viewModel.fetchTourPoints(currentCityId)

        binding.tpSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

        binding.tpTopAppBar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                val it = item ?: return false
                return when (it.itemId) {
                    R.id.tour_points_filter_show_all -> {
                        viewModel.sortQuery = FilterType.ALL
                        viewModel.applyFilterAndSort()
                        binding.tpSearchView.setQuery("", false)
                        true
                    }
                    R.id.tour_points_filter_by_category -> {
                        viewModel.sortQuery = FilterType.CATEGORY
                        viewModel.applyFilterAndSort()
                        true
                    }
                    else -> false
                }
            }
        })
    }

    private fun setRecycler() {
        binding.recyclerTourPoints.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTourPoints.adapter = TourPointsAdapter(mutableListOf(), this)
    }

    override fun gotoTourPoint(tourPointPath: TourPointPath, position: Int) {
        // TODO
    }
}
