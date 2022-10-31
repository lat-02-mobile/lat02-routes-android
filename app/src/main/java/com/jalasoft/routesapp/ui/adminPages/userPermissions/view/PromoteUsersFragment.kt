package com.jalasoft.routesapp.ui.adminPages.userPermissions.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private fun setRecycler() {
        binding.recyclerUsersAdministrator.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsersAdministrator.adapter = UserAdapter(mutableListOf(), this)
    }

    override fun addRevokeUserPermission(user: User) {
    }
}
