package com.jalasoft.routesapp.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jalasoft.routesapp.data.model.remote.Line
import com.jalasoft.routesapp.databinding.PossibleRoutesBottomSheetBinding
import com.jalasoft.routesapp.ui.routes.adapters.PossibleRouteAdapter

class PossibleRoutesBottomSheet(var list: List<Line>): BottomSheetDialogFragment(), PossibleRouteAdapter.IPossibleRouteListener {

    private var _binding: PossibleRoutesBottomSheetBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "PossibleRoutesBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PossibleRoutesBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.recyclerPossibleRoutes

    }

    private fun setRecycler() {
        binding.recyclerPossibleRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPossibleRoutes.adapter = PossibleRouteAdapter(linesList = mutableListOf(), this)
    }

    private fun updateRecycler(list: List<Line>) {
        (binding.recyclerPossibleRoutes.adapter as PossibleRouteAdapter).updateList(list.toMutableList())
    }

    override fun onCityTap(line: Line) {
        TODO("DRAW ROUTE")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}