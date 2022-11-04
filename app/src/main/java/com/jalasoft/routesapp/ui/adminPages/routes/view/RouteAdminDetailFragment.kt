package com.jalasoft.routesapp.ui.adminPages.routes.view

import android.graphics.Color
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
import com.jalasoft.routesapp.data.model.local.LineRouteAux
import com.jalasoft.routesapp.databinding.FragmentRouteAdminDetailBinding
import com.jalasoft.routesapp.ui.adminPages.routes.viewModel.RoutesAdminViewModel
import com.jalasoft.routesapp.util.Extensions.toEditable
import com.jalasoft.routesapp.util.helpers.Constants
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteAdminDetailFragment : Fragment() {
    private var _binding: FragmentRouteAdminDetailBinding? = null
    private val binding get() = _binding!!
    private var defaultHexColor = "#004696"
    private val viewModel: RoutesAdminViewModel by viewModels()

    private lateinit var lineRouteInfo: LineRouteAux

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteAdminDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isNew = arguments?.getBoolean(Constants.BUNDLE_KEY_NEW_ROUTE) ?: true
        lineRouteInfo = arguments?.getSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA) as LineRouteAux
        if (!isNew) {
            binding.routeName.text = lineRouteInfo.name.toEditable()
            binding.routeVelocity.text = lineRouteInfo.velocity.toString().toEditable()
            defaultHexColor = lineRouteInfo.color
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.colorPreview.setBackgroundColor(Color.parseColor(defaultHexColor))
        binding.btnColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(requireContext()).setTitle(getString(R.string.choose_color))
                .setPositiveButton(
                    getString(R.string.ok),
                    ColorEnvelopeListener { envelope, _ ->
                        defaultHexColor = "#${envelope.hexCode}"
                        binding.colorPreview.setBackgroundColor(Color.parseColor(defaultHexColor))
                    }
                )
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
        }
        binding.btnSave.setOnClickListener {
            lineRouteInfo.color = defaultHexColor
            val name = binding.routeName.text.toString()
            if (name.isEmpty() || name.length < 5) {
                binding.nameLayout.error = getString(R.string.route_name_error)
                return@setOnClickListener
            }
            binding.nameLayout.error = null
            val velocity = binding.routeVelocity.text.toString()
            if (velocity.isEmpty()) {
                binding.velocityLayout.error = getString(R.string.field_empty)
                return@setOnClickListener
            } else {
                try {
                    val auxVel = binding.routeVelocity.text.toString().toDouble()
                    if (auxVel <= 0) {
                        binding.velocityLayout.error = getString(R.string.velocity_greater_than_zero)
                        return@setOnClickListener
                    } else {
                        binding.velocityLayout.error = null
                        lineRouteInfo.name = name
                        lineRouteInfo.velocity = binding.routeVelocity.text.toString().toDouble()
                        binding.progressBar.visibility = View.VISIBLE
                        viewModel.saveRoute(lineRouteInfo, isNew)
                    }
                } catch (e: NumberFormatException) {
                    binding.velocityLayout.error = getString(R.string.velocity_format_error)
                }
            }
        }
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
        val successObserver = Observer<Unit> {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), R.string.route_info_updated, Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.successSave.observe(this, successObserver)
    }
}
