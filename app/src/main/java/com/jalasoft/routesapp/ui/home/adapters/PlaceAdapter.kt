package com.jalasoft.routesapp.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.databinding.PlaceItemBinding

class PlaceAdapter(var placesList: MutableList<Place>, val listener: IPlaceListener) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    interface IPlaceListener {
        fun onPlaceTap(place: Place)
    }

    class PlaceViewHolder(val binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaceItemBinding.inflate(inflater, parent, false)
        val placeViewHolder = PlaceViewHolder(binding)
        return placeViewHolder
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.binding.ivAddressIcon.load(placesList[position].icon)
        holder.binding.placeItem = placesList[position]
        holder.binding.placeContainer.setOnClickListener {
            listener.onPlaceTap(placesList[position])
        }
    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    fun updateList(placesList: MutableList<Place>) {
        val oldSize = this.placesList.size
        this.placesList = placesList
        if (placesList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, placesList.size)
        } else {
            notifyItemRangeChanged(0, this.placesList.size)
        }
    }
}
