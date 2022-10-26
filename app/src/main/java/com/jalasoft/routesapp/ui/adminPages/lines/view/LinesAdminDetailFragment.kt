package com.jalasoft.routesapp.ui.adminPages.lines.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.LineAux
import com.jalasoft.routesapp.data.model.remote.LineCategories
import com.jalasoft.routesapp.databinding.FragmentLinesAdminDetailBinding
import com.jalasoft.routesapp.ui.adminPages.lines.adapter.SpinnerCityAdapter
import com.jalasoft.routesapp.ui.adminPages.lines.adapter.SpinnerLineCategoryAdapter
import com.jalasoft.routesapp.ui.adminPages.lines.viewModel.LinesAdminViewModel
import com.jalasoft.routesapp.util.Extensions.toEditable
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LinesAdminDetailFragment : Fragment() {

    private var _binding: FragmentLinesAdminDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LinesAdminViewModel by viewModels()
    private var line: LineAux? = null
    private var isNew: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLinesAdminDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isNew = arguments?.getSerializable(Constants.BUNDLE_KEY_LINE_ADMIN_IS_NEW) as Boolean
        if (isNew) {
            binding.btnEditSave.text = RoutesAppApplication.resource?.getString(R.string.linesa_btn_save).toString()
        } else {
            line = arguments?.getSerializable(Constants.BUNDLE_KEY_LINE_ADMIN_SELECTED_DATA) as LineAux
            loadData(line)
            binding.btnEditSave.text = RoutesAppApplication.resource?.getString(R.string.linesa_btn_edit).toString()
        }

        viewModel.lineCategories.observe(viewLifecycleOwner) {
            setCategorySpinner(it.toMutableList())
        }
        viewModel.cities.observe(viewLifecycleOwner) {
            setCitySpinner(it.toMutableList())
        }
        viewModel.getLineCategories()
        viewModel.getCities()
        buttonActions()
    }

    private fun loadData(line: LineAux?) {
        val name = line?.name ?: ""
        binding.etName.text = name.toEditable()
        binding.swEnable.isChecked = line?.enable ?: true
    }

    private fun buttonActions() {
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = viewModel.lineCategories.value?.get(position)
                viewModel.categorySelected = item?.id ?: ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.citySelected = ""
            }
        }

        binding.spCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = viewModel.cities.value?.get(position)
                viewModel.citySelected = item?.id ?: ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.citySelected = ""
            }
        }

        binding.swEnable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.enable = isChecked
        }

        binding.btnEditSave.setOnClickListener {
            if (isNew) {
                val name = binding.etName.text.toString()
                viewModel.saveNewLine(name)
            } else {
            }
        }
    }

    private fun setCategorySpinner(list: List<LineCategories>) {
        val adapter = SpinnerLineCategoryAdapter(requireContext(), list)
        binding.spCategory.adapter = adapter
    }

    private fun setCitySpinner(list: List<City>) {
        val adapter = SpinnerCityAdapter(requireContext(), list)
        binding.spCity.adapter = adapter
    }
}
