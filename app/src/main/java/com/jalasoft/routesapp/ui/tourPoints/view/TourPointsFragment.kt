package com.jalasoft.routesapp.ui.tourPoints.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.databinding.FragmentTourPointsBinding
import com.jalasoft.routesapp.ui.tourPoints.adapter.TourPointsAdapter
import com.jalasoft.routesapp.ui.tourPoints.viewModel.TourPointsViewModel
import com.jalasoft.routesapp.util.PreferenceManager
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
    }

    private fun setRecycler() {
        binding.recyclerTourPoints.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTourPoints.adapter = TourPointsAdapter(mutableListOf(), this)
    }

    override fun gotoTourPoint(tourPointPath: TourPointPath, position: Int) {
        // TODO
    }
}
