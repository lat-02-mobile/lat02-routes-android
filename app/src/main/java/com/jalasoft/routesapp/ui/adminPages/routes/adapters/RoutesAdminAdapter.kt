package com.jalasoft.routesapp.ui.adminPages.routes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.databinding.RouteAdminItemBinding

class RoutesAdminAdapter(var routeList: List<LineRouteInfo>, val listener: IRoutesAdminListener) : RecyclerView.Adapter<RoutesAdminAdapter.RoutesAdminViewHolder> () {

    interface IRoutesAdminListener {
        fun onRouteTap(routeInfo: LineRouteInfo)
    }

    class RoutesAdminViewHolder(val binding: RouteAdminItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutesAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RouteAdminItemBinding.inflate(inflater, parent, false)
        return RoutesAdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutesAdminViewHolder, position: Int) {
        val route = routeList[position]
        holder.binding.name.text = route.name
        holder.binding.pointsCount.text = holder.binding.root.resources.getString(R.string.route_points, route.routePoints.size)
        holder.binding.container.setOnClickListener {
            listener.onRouteTap(route)
        }
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    fun updateList(list: List<LineRouteInfo>) {
        val oldSize = this.routeList.size
        this.routeList = list
        if (list.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, list.size)
        } else {
            notifyItemRangeChanged(0, this.routeList.size)
        }
    }
}
