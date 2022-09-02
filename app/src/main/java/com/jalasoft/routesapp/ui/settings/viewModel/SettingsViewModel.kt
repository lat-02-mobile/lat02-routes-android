package com.jalasoft.routesapp.ui.settings.viewModel

import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val repository: CityRepository) : ViewModel()
