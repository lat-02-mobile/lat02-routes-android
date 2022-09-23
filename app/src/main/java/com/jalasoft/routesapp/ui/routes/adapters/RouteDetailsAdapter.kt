package com.jalasoft.routesapp.ui.routes.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.local.RouteDetail
import com.jalasoft.routesapp.databinding.PossibleRouteItemBinding
import com.jalasoft.routesapp.util.helpers.WalkDirection

class RouteDetailsAdapter(var routesList: MutableList<RouteDetail>) : RecyclerView.Adapter<RouteDetailsAdapter.RouteDetailViewHolder>() {

    class RouteDetailViewHolder(val binding: PossibleRouteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PossibleRouteItemBinding.inflate(inflater, parent, false)
        return RouteDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteDetailViewHolder, position: Int) {
        val routeDetail = routesList[position]
        holder.binding.itemIndex.setTextColor(Color.BLACK)
        holder.binding.recommendedText.setTextColor(Color.BLACK)
        holder.binding.routeName.setTextColor(Color.BLACK)
        holder.binding.estimatedTimeText.setTextColor(Color.BLACK)
        holder.binding.minText.setTextColor(Color.BLACK)
        holder.binding.transportImage.setBackgroundColor(Color.WHITE)
        holder.binding.transportImage.setColorFilter(Color.BLACK)
        holder.binding.recommendedText.visibility = View.GONE
        holder.binding.routeName.text = when (routeDetail.walkDirection) {
            WalkDirection.TO_FIRST_STOP -> holder.binding.root.context.getString(R.string.walk_to_first_stop)
            WalkDirection.TO_NEXT_STOP -> holder.binding.root.context.getString(R.string.walk_to_next_stop)
            WalkDirection.TO_DESTINATION -> holder.binding.root.context.getString(R.string.walk_to_destination)
            WalkDirection.IS_NOT_WALKING -> routeDetail.name
        }
        if (routeDetail.walkDirection != WalkDirection.IS_NOT_WALKING) {
            holder.binding.transportImage.setImageResource(R.drawable.ic_baseline_directions_walk_24)
        } else {
            holder.binding.transportImage.load(routeDetail.icon.blackUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_bus_stop)
                transformations(CircleCropTransformation())
            }
        }
        holder.binding.distanceUnit.setTextColor(Color.BLACK)
        holder.binding.estimatedDistanceText.setTextColor(Color.BLACK)

        val estimatedDistance = "${routeDetail.estimatedDistance} "
        holder.binding.estimatedDistanceText.text = estimatedDistance
        val estimatedTime = "${routeDetail.estimatedTime} "
        holder.binding.estimatedTimeText.text = estimatedTime
    }

    override fun getItemCount(): Int {
        return routesList.size
    }

    fun updateList(linesList: MutableList<RouteDetail>) {
        val oldSize = this.routesList.size
        this.routesList = linesList
        if (linesList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, linesList.size)
        } else {
            notifyItemRangeChanged(0, this.routesList.size)
        }
    }
}
