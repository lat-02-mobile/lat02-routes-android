package com.jalasoft.routesapp.ui.adminPages.lines.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LineAux
import com.jalasoft.routesapp.databinding.FragmentLinesAdminBinding
import com.jalasoft.routesapp.ui.adminPages.lines.adapter.LinesAdminAdapter
import com.jalasoft.routesapp.ui.adminPages.lines.viewModel.LinesAdminViewModel
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LinesAdminFragment : Fragment(), LinesAdminAdapter.ILinesAdminListener {

    private var _binding: FragmentLinesAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LinesAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLinesAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()

        binding.progressBar.visibility = View.VISIBLE
        viewModel.lineList.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            (binding.recyclerLinesAdministrator.adapter as LinesAdminAdapter).updateList(it.toMutableList())
        }
        viewModel.fetchLines()

        binding.lapSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
    }

    private fun setRecycler() {
        binding.recyclerLinesAdministrator.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLinesAdministrator.adapter = LinesAdminAdapter(mutableListOf(), this)
    }

    override fun gotoEditLine(lineAux: LineAux) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.BUNDLE_KEY_LINE_ADMIN_SELECTED_DATA, lineAux)
        findNavController().navigate(R.id.linesAdminDetailFragment, bundle)
    }
}
