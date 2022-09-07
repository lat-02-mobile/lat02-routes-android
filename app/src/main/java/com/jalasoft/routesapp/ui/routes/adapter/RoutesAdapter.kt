package com.jalasoft.routesapp.ui.routes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.databinding.RouteItemBinding

class RoutesAdapter(var routeList: MutableList<LinePath>, val listener: IRoutesListener) : RecyclerView.Adapter<RoutesAdapter.RouteViewHolder>() {

    interface IRoutesListener {
        fun gotoRoute(route: LinePath, position: Int)
    }

    class RouteViewHolder(val binding: RouteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RouteItemBinding.inflate(inflater, parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routeList[position]
        holder.binding.routeItem = route
        holder.binding.container.setOnClickListener {
            listener.gotoRoute(route, position)
        }
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    fun updateList(routesList: MutableList<LinePath>) {
        val oldSize = this.routeList.size
        this.routeList = routesList
        if (routesList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, routesList.size)
        } else {
            notifyItemRangeChanged(0, this.routeList.size)
        }
    }
}
