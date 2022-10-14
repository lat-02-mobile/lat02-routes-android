package com.jalasoft.routesapp.ui.favorites.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel
@Inject
constructor(private val localDB: LocalDataBaseRepository) : ViewModel() {

    val favoritesList: MutableLiveData<List<FavoriteDestinationEntity>> by lazy {
        MutableLiveData<List<FavoriteDestinationEntity>>(listOf())
    }

    private var originalList: MutableList<FavoriteDestinationEntity> = mutableListOf()

    fun getFavoriteDestinationsByCityAndUserId(context: Context): List<FavoriteDestinationEntity> {
        val data = localDB.getFavoriteDestinationByCityAndUserId(context)
        favoritesList.value = data
        originalList = (favoritesList.value as MutableList<FavoriteDestinationEntity>).toMutableList()
        return data
    }

    fun updateFavoriteDestination(name: String, favDest: FavoriteDestinationEntity) {
        favDest.name = name
        localDB.editFavoriteDestination(favDest)
    }

    fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity, index: Int) {
        originalList.removeAt(index)
        favoritesList.value = originalList
        localDB.deleteFavoriteDestination(favoriteDestinationEntity)
    }

    fun filterFavorites(criteria: String): Int {
        favoritesList.value = originalList.filter { favorite ->
            favorite.name.lowercase().contains(criteria.lowercase())
        }.toMutableList()
        return favoritesList.value!!.size
    }
}
