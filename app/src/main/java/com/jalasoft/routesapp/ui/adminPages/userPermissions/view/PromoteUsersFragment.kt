package com.jalasoft.routesapp.ui.adminPages.userPermissions.view

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.databinding.FragmentPromoteUsersBinding
import com.jalasoft.routesapp.ui.adminPages.userPermissions.adapter.UserAdapter
import com.jalasoft.routesapp.ui.adminPages.userPermissions.viewModel.PromoteUsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoteUsersFragment : Fragment(), UserAdapter.IUserListener {

    private var _binding: FragmentPromoteUsersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PromoteUsersViewModel by viewModels()
    private lateinit var userSelected: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPromoteUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecycler()

        binding.progressBar.visibility = View.VISIBLE
        viewModel.users.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            (binding.recyclerUsersAdministrator.adapter as UserAdapter).updateList(it.toMutableList())
        }
        viewModel.fetchUsers()

        binding.uppSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchQuery = query.toString()
                viewModel.applyFilter()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchQuery = newText.toString()
                viewModel.applyFilter()
                return true
            }
        })

        binding.fadUserPermissions.setOnClickListener {
            val builder = AlertDialog.Builder(binding.root.context)
            if (userSelected.type == 0) {
                builder.setTitle(R.string.promote_user)
                builder.setMessage(requireContext().getString(R.string.sure_promote_user, userSelected.name))
                builder.setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.promoteUser(userSelected)
                }
                builder.setNegativeButton(R.string.cancel) { _, _ ->
                }
                builder.show()
            } else {
                builder.setTitle(R.string.revoke_user)
                builder.setMessage(requireContext().getString(R.string.sure_revoke_user, userSelected.name))
                builder.setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.revokeUserPermission(userSelected)
                }
                builder.setNegativeButton(R.string.cancel) { _, _ ->
                }
                builder.show()
            }
        }
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle(errorMessage)
            builder.setMessage(errorMessage)
            builder.setPositiveButton(R.string.cancel) { _, _ ->
            }
            builder.show()
            showProgress(false)
        }
        val successResult = Observer<Boolean> { successResult ->
            if (successResult) {
                showProgress(false)
                Toast.makeText(context, requireContext().getString(R.string.succes_result_user), Toast.LENGTH_SHORT).show()
                binding.fadUserPermissions.visibility = View.GONE
                viewModel.fetchUsers()
            } else {
                showProgress(false)
            }
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.successResult.observe(this, successResult)
    }

    private fun setRecycler() {
        binding.recyclerUsersAdministrator.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsersAdministrator.adapter = UserAdapter(mutableListOf(), this)
    }

    override fun addRevokeUserPermission(user: User) {
        if (user.type == 0) {
            binding.fadUserPermissions.visibility = View.VISIBLE
            binding.fadUserPermissions.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.color_primary, null))
            binding.fadUserPermissions.setImageResource(R.drawable.ic_user_settings_line)
            userSelected = user
        } else {
            binding.fadUserPermissions.visibility = View.VISIBLE
            binding.fadUserPermissions.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.delete_color, null))
            binding.fadUserPermissions.setImageResource(R.drawable.ic_settings_alert_outline)
            userSelected = user
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
