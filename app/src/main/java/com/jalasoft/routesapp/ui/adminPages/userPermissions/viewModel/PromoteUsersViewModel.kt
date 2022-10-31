package com.jalasoft.routesapp.ui.adminPages.userPermissions.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.interfaces.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromoteUsersViewModel
@Inject
constructor(private val repository: UserRepository) : ViewModel() {
    var _usersList: MutableLiveData<List<User>> = MutableLiveData()
    val users: LiveData<List<User>> = _usersList
    var originalList: List<User> = listOf()
    var searchQuery = ""

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val successResult: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun fetchUsers() = viewModelScope.launch {
        _usersList.value = repository.getAllUsers()
        originalList = _usersList.value ?: listOf()
    }

    fun applyFilter() {
        if (searchQuery.isEmpty()) return
        var filteredTourPoints = originalList
        filteredTourPoints = filterByQuery(searchQuery, filteredTourPoints)
        _usersList.value = filteredTourPoints
    }

    private fun filterByQuery(query: String, usersList: List<User>): List<User> {
        if (searchQuery.isEmpty()) return usersList
        return usersList.filter { user ->
            val name = user.name ?: return@filter false
            name.lowercase().contains(query.lowercase())
        }
    }
}
