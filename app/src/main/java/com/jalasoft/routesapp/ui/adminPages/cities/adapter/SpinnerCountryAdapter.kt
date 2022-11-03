package com.jalasoft.routesapp.ui.adminPages.cities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.Country

class SpinnerCountryAdapter(context: Context, countryList: List<Country>) : ArrayAdapter<Country>(context, 0, countryList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return myView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return myView(position, convertView, parent)
    }

    private fun myView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)

        item.let {
            val text = view.findViewById<TextView>(R.id.tv_spinner_description)
            text.text = item?.name ?: ""
        }
        return view
    }
}
