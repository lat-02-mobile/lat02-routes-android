package com.jalasoft.routesapp.ui.adminPages.lines.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
    private var editCategoryList: List<LineCategories> = listOf()
    private var editCityList: List<City> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
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

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle(R.string.field_empty)
            builder.setMessage(errorMessage)
            builder.setPositiveButton(R.string.ok) { _, _ ->
            }
            builder.show()
            showProgress(false)
        }
        val successResult = Observer<Boolean> { successResult ->
            if (successResult) {
                val message = if (isNew) RoutesAppApplication.resource?.getString(R.string.register_success).toString() else RoutesAppApplication.resource?.getString(R.string.edited_success).toString()
                showProgress(false)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                showProgress(false)
            }
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.successResult.observe(this, successResult)
    }

    private fun loadData(line: LineAux?) {
        val name = line?.name ?: ""
        binding.etName.text = name.toEditable()
        binding.swEnable.isChecked = line?.enable ?: true
    }

    private fun buttonActions() {
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isNew) {
                    val item = viewModel.lineCategories.value?.get(position)
                    viewModel.categorySelected = item?.id ?: ""
                } else {
                    val item = editCategoryList[position]
                    viewModel.categorySelected = item.id
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (isNew) {
                    viewModel.categorySelected = ""
                } else {
                    viewModel.categorySelected = line?.idCategory ?: ""
                }
            }
        }

        binding.spCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isNew) {
                    val item = viewModel.cities.value?.get(position)
                    viewModel.citySelected = item?.id ?: ""
                } else {
                    val item = editCityList[position]
                    viewModel.citySelected = item.id
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (isNew) {
                    viewModel.citySelected = ""
                } else {
                    viewModel.citySelected = line?.idCity ?: ""
                }
            }
        }

        binding.swEnable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.enable = isChecked
        }

        binding.btnEditSave.setOnClickListener {
            if (isNew) {
                showProgress(true)
                val name = binding.etName.text.toString()
                viewModel.saveNewLine(name)
            } else {
                showProgress(true)
                val name = binding.etName.text.toString()
                val id = line?.id ?: ""
                viewModel.updateLine(id, name)
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setCategorySpinner(list: List<LineCategories>) {
        if (isNew) {
            val adapter = SpinnerLineCategoryAdapter(requireContext(), list)
            binding.spCategory.adapter = adapter
        } else {
            val categoryFiltered: List<LineCategories> = list.filter { it.id == line?.idCategory }
            val listFiltered: List<LineCategories> = list.filterNot { it.id == line?.idCategory }
            var joinList: ArrayList<LineCategories> = arrayListOf()
            joinList.addAll(0, categoryFiltered)
            joinList.addAll(listFiltered)
            val adapter = SpinnerLineCategoryAdapter(requireContext(), joinList)
            binding.spCategory.adapter = adapter
            editCategoryList = joinList
        }
    }

    private fun setCitySpinner(list: List<City>) {
        if (isNew) {
            val adapter = SpinnerCityAdapter(requireContext(), list)
            binding.spCity.adapter = adapter
        } else {
            val cityFiltered: List<City> = list.filter { it.id == line?.idCity }
            val listFiltered: List<City> = list.filterNot { it.id == line?.idCity }
            var joinList: ArrayList<City> = arrayListOf()
            joinList.addAll(0, cityFiltered)
            joinList.addAll(listFiltered)
            val adapter = SpinnerCityAdapter(requireContext(), joinList)
            binding.spCity.adapter = adapter
            editCityList = joinList
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.pbRegUser.visibility = View.VISIBLE
        } else {
            binding.pbRegUser.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
