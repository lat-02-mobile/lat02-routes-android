package com.jalasoft.routesapp.ui.tourPoints.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.databinding.TourPointItemBinding

class TourPointsAdapter(var tourPointsList: MutableList<TourPointPath>, val listener: ITourPointsListener) : RecyclerView.Adapter<TourPointsAdapter.TourPointViewHolder> () {
    interface ITourPointsListener {
        fun gotoTourPoint(tourPointPath: TourPointPath, position: Int)
    }

    class TourPointViewHolder(val binding: TourPointItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourPointViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TourPointItemBinding.inflate(inflater, parent, false)
        return TourPointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TourPointViewHolder, position: Int) {
        holder.binding.tourPointItem = tourPointsList[position]
        holder.binding.tourPointContainer.setOnClickListener {
            listener.gotoTourPoint(tourPointsList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return tourPointsList.size
    }

    fun updateList(tourPointsList: MutableList<TourPointPath>) {
        val oldSize = this.tourPointsList.size
        this.tourPointsList = tourPointsList
        if (tourPointsList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, tourPointsList.size)
        } else {
            notifyItemRangeChanged(0, this.tourPointsList.size)
        }
    }
}
