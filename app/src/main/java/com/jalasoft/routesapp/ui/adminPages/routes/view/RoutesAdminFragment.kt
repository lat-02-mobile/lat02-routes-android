package com.jalasoft.routesapp.ui.adminPages.routes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.local.LineRouteAux
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.databinding.FragmentRoutesAdminBinding
import com.jalasoft.routesapp.ui.adminPages.routes.adapters.RoutesAdminAdapter
import com.jalasoft.routesapp.ui.adminPages.routes.viewModel.RoutesAdminViewModel
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutesAdminFragment : Fragment(), RoutesAdminAdapter.IRoutesAdminListener {
    private var _binding: FragmentRoutesAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoutesAdminViewModel by viewModels()

    private lateinit var alertDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutesAdminBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()
        val lineId = arguments?.getString(Constants.BUNDLE_KEY_LINE_ID) ?: ""
        alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(getString(R.string.select_option))
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabNewRoute.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(Constants.BUNDLE_KEY_NEW_ROUTE, true)
            bundle.putSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA, LineRouteAux(lineId))
            findNavController().navigate(R.id.action_routesAdminFragment_to_routeAdminDetailFragment, bundle)
        }
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getAllRoutesForLine(lineId)
    }

    private fun setRecycler() {
        binding.recyclerRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRoutes.adapter = RoutesAdminAdapter(mutableListOf(), this)
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
        val successObserver = Observer<List<LineRouteInfo>> {
            binding.progressBar.visibility = View.GONE
            (binding.recyclerRoutes.adapter as RoutesAdminAdapter).updateList(it)
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.successFetching.observe(this, successObserver)
    }

    override fun onRouteTap(routeInfo: LineRouteInfo) {
        val options = listOf(getString(R.string.go_to_map_editor), getString(R.string.see_route_details))
        val bundle = Bundle()

        alertDialog.setItems(options.toTypedArray()) { dialog, position ->
            if (position == 0) {
                bundle.putSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA, routeInfo)
                findNavController().navigate(R.id.routeEditorFragment, bundle)
            } else {
                val route = LineRouteAux(routeInfo.idLine, routeInfo.id, routeInfo.name, routeInfo.color, routeInfo.averageVelocity)
                bundle.putSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA, route)
                bundle.putBoolean(Constants.BUNDLE_KEY_NEW_ROUTE, false)
                findNavController().navigate(R.id.action_routesAdminFragment_to_routeAdminDetailFragment, bundle)
            }
            dialog.dismiss()
        }
        val dialog = alertDialog.create()
        dialog.show()
    }
}
