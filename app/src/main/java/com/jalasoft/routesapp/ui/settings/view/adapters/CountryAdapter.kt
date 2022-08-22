package com.jalasoft.routesapp.ui.settings.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.databinding.CountryItemBinding

class CountryAdapter(var countryList: MutableList<Country>, val listener: ICountryListener) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    interface ICountryListener {
        fun onCountryTap(country: Country)
    }

    class CountryViewHolder(val binding: CountryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData() {
            val data = "f"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CountryItemBinding.inflate(inflater, parent, false)
        val countryViewHolder = CountryViewHolder(binding)
        return countryViewHolder
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return countryList.size
    }
}
