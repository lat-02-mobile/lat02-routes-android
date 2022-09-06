package com.jalasoft.routesapp.ui.routes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.jalasoft.routesapp.data.model.remote.Line
import com.jalasoft.routesapp.databinding.PossibleRouteItemBinding

class PossibleRouteAdapter (var linesList: MutableList<Line>, val listener: IPossibleRouteListener) : RecyclerView.Adapter<PossibleRouteAdapter.PossibleRouteViewHolder>() {

    interface IPossibleRouteListener {
        fun onCityTap(line: Line)
    }

    class PossibleRouteViewHolder(val binding: PossibleRouteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PossibleRouteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PossibleRouteItemBinding.inflate(inflater, parent, false)
        return PossibleRouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PossibleRouteViewHolder, position: Int) {
        val line = linesList[position]
        if (position == 0) holder.binding.lineNameText.text = line.name + "(recommended)"
        else holder.binding.lineNameText.text = line.name
//        val imageLoader = ImageLoader.Builder(holder.binding.root.context)
//            .components {
//                add(SvgDecoder.Factory())
//            }
//            .build()
//        val request = ImageRequest.Builder(holder.binding.root.context)
//            .crossfade(true)
//            .crossfade(500)
//            .data(line.categoryRef)
//            .target(holder.binding.transportImage)
//            .build()
//
//        imageLoader.enqueue(request)
        holder.binding.possibleRouteContainer.setOnClickListener {
            listener.onCityTap(line)
        }
    }

    override fun getItemCount(): Int {
        return linesList.size
    }

    fun updateList(linesList: MutableList<Line>) {
        val oldSize = this.linesList.size
        this.linesList = linesList
        if (linesList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, linesList.size)
        } else {
            notifyItemRangeChanged(0, this.linesList.size)
        }
    }

}
