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
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
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
    private lateinit var lineRoutePath: LineRoutePath
    private var positionLineRoutePath: Int = 0
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

        viewModel.lineRouteList.observe(viewLifecycleOwner) {
            when {
                it.size > 1 -> {
                    var routeListName = arrayOf<String> ()
                    for (i in it.indices) {
                        routeListName += (it[i].name)
                    }
                    alertDialog.setItems(routeListName) { dialog, position ->
                        dialog.dismiss()
                        lineRoutePath = it[position]
                        positionLineRoutePath = position
                        viewModel.cleanLineRouteList()
                        goToSelectedRoute(lineRoutePath, positionLineRoutePath)
                    }
                    val dialog = alertDialog.create()
                    dialog.show()
                }
                it.size == 1 -> {
                    goToSelectedRoute(it[0], 0)
                }
            }
        }
        viewModel.fetchLines(requireContext())
    }

    private fun setRecycler() {
        binding.recyclerRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRoutes.adapter = RoutesAdapter(mutableListOf(), this)
    }

    override fun fetchLineRoute(route: LineInfo, position: Int) {
        viewModel.fetchLineRoute(route.idLine)
    }

    private fun goToSelectedRoute(route: LineRoutePath, position: Int) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA, route)
        bundle.putSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_POSITION, position)
        findNavController().navigate(R.id.routeSelected, bundle)
    }
}
