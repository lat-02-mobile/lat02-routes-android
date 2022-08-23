package com.jalasoft.routesapp.ui.cityPicker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.databinding.CityItemBinding

class CityAdapter(var citiesList: MutableList<City>, val listener: ICityListener) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    interface ICityListener {
        fun onCountryTap(city: City)
    }

    class CityViewHolder(val binding: CityItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CityItemBinding.inflate(inflater, parent, false)
        val countryViewHolder = CityViewHolder(binding)
        return countryViewHolder
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.binding.cityItem = citiesList[position]
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }

    fun updateList(citiesList: MutableList<City>) {
        this.citiesList = citiesList
        notifyItemRangeChanged(0, citiesList.size)
    }
}
