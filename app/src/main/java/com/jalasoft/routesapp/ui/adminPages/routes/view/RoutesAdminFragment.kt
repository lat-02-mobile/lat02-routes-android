package com.jalasoft.routesapp.ui.adminPages.routes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        val lineId = arguments?.getString(Constants.BUNDLE_KEY_LINE_ID) ?: ""
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
        println(routeInfo.id)
    }
}
