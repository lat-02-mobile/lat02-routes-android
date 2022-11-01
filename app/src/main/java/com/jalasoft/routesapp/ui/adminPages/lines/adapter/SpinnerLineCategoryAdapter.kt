package com.jalasoft.routesapp.ui.adminPages.lines.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LineCategories
import com.jalasoft.routesapp.util.helpers.Constants
import java.util.*

class SpinnerLineCategoryAdapter(context: Context, lineCategoryList: List<LineCategories>) : ArrayAdapter<LineCategories>(context, 0, lineCategoryList) {
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
            val currLang = Locale.getDefault().isO3Language
            val text = view.findViewById<TextView>(R.id.tv_spinner_description)
            text.text = if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) item?.nameEsp ?: ""
            else item?.nameEng ?: ""
        }
        return view
    }
}
