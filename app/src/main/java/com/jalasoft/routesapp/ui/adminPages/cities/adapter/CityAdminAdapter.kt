package com.jalasoft.routesapp.ui.adminPages.cities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.databinding.CityAdminItemBinding

class CityAdminAdapter(var citiesList: MutableList<City>, val listener: ICityAdminListener) : RecyclerView.Adapter<CityAdminAdapter.CityAdminViewHolder> () {
    interface ICityAdminListener {
        fun gotoEditCity(city: City)
    }

    class CityAdminViewHolder(val binding: CityAdminItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CityAdminItemBinding.inflate(inflater, parent, false)
        return CityAdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityAdminViewHolder, position: Int) {
        holder.binding.cityItem = citiesList[position]
        holder.binding.cityAdminContainer.setOnClickListener {
            listener.gotoEditCity(citiesList[position])
        }
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }

    fun updateList(citiesList: MutableList<City>) {
        val oldSize = this.citiesList.size
        this.citiesList = citiesList
        if (citiesList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, citiesList.size)
        } else {
            notifyItemRangeChanged(0, this.citiesList.size)
        }
    }
}
