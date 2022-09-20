package com.jalasoft.routesapp.ui.routes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LineInfo
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.databinding.FragmentRoutesBinding
import com.jalasoft.routesapp.ui.routes.adapters.RoutesAdapter
import com.jalasoft.routesapp.ui.routes.viewModel.RoutesViewModel
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutesFragment : Fragment(), RoutesAdapter.IRoutesListener {
    private var _binding: FragmentRoutesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoutesViewModel by viewModels()
    private lateinit var alertDialog: AlertDialog.Builder

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
        alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(getString(R.string.select_route))
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterLines(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterLines(newText.toString())
                return true
            }
        })

        viewModel.routesList.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            (binding.recyclerRoutes.adapter as RoutesAdapter).updateList(it.toMutableList())
        }

        viewModel.fetchLines(requireContext())
    }

    private fun setRecycler() {
        binding.recyclerRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRoutes.adapter = RoutesAdapter(mutableListOf(), this)
    }

    override fun onLineTap(route: LineInfo) {
        when {
            route.routePaths.size > 1 -> {
                val routeListName = route.routePaths.map {
                    it.name
                }
                alertDialog.setItems(routeListName.toTypedArray()) { dialog, position ->
                    val lineRouteInfo = route.routePaths[position]
                    goToSelectedRoute(lineRouteInfo)
                    dialog.dismiss()
                }
                val dialog = alertDialog.create()
                dialog.show()
            }
            route.routePaths.size == 1 -> {
                goToSelectedRoute(route.routePaths[0])
            }
        }
    }

    private fun goToSelectedRoute(route: LineRouteInfo) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA, route)
        findNavController().navigate(R.id.routeSelected, bundle)
    }
}
