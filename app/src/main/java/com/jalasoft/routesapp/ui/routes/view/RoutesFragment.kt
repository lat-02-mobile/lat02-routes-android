package com.jalasoft.routesapp.ui.routes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.databinding.FragmentRoutesBinding
import com.jalasoft.routesapp.ui.routes.adapter.RoutesAdapter
import com.jalasoft.routesapp.ui.routes.viewModel.RoutesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutesFragment : Fragment(), RoutesAdapter.IRoutesListener {
    private var _binding: FragmentRoutesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoutesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()
        binding.progressBar.visibility = View.VISIBLE

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterRoutes(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterRoutes(newText.toString())
                return true
            }
        })

        viewModel.routesList.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            (binding.recyclerRoutes.adapter as RoutesAdapter).updateList(it.toMutableList())
        }

        viewModel.fetchRoutes()
    }

    private fun setRecycler() {
        binding.recyclerRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRoutes.adapter = RoutesAdapter(mutableListOf(), this)
    }

    override fun gotoRoute(route: LinePath, position: Int) {
        val bundle = Bundle()
        bundle.putSerializable("routeSelected", route)
        bundle.putSerializable("routeSelectedPosition", position)
        findNavController().navigate(R.id.routeSelected, bundle)
    }
}
