package com.jalasoft.routesapp.ui.adminPages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LineRoute
import com.jalasoft.routesapp.databinding.FragmentAdminBinding
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLinesAdmin.setOnClickListener {
            findNavController().navigate(R.id.linesAdminFragment)
        }

        binding.btnRouteEditor.setOnClickListener {
            viewModel.callRouteDetails()
        }

        binding.btnUserPromote.setOnClickListener {
            findNavController().navigate(R.id.promoteUsersFragment)
        }

        binding.btnCities.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_cityAdminFragment)
        }
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
        val routeObserver = Observer<LineRoute> { lineRoute ->
            val bundle = Bundle()
            bundle.putSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA, lineRoute.lineRouteToLineRouteInfo())
            findNavController().navigate(R.id.routeEditorFragment, bundle)
        }

        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.lineRoute.observe(this, routeObserver)
    }
}
