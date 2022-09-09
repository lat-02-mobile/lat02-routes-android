package com.jalasoft.routesapp.ui.routes.adapters

import android.annotation.SuppressLint
import android.graphics.Color.*
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.request.ImageRequest
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.Line
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.databinding.PossibleRouteItemBinding

class PossibleRouteAdapter (var linesList: MutableList<LinePath>, val listener: IPossibleRouteListener) : RecyclerView.Adapter<PossibleRouteAdapter.PossibleRouteViewHolder>() {

    private var lastSelectedPosition = -1

    interface IPossibleRouteListener {
        fun onCityTap(line: LinePath)
    }

    class PossibleRouteViewHolder(val binding: PossibleRouteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PossibleRouteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PossibleRouteItemBinding.inflate(inflater, parent, false)
        return PossibleRouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PossibleRouteViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val line = linesList[position]
        if (position == 0) holder.binding.recommendedText.visibility = View.VISIBLE
        else holder.binding.recommendedText.visibility = View.GONE
        holder.binding.lineNameText.text = line.name
        val primaryColor = ContextCompat.getColor(holder.binding.root.context, R.color.color_primary_gradient)
        if (lastSelectedPosition == position) {
            holder.binding.lineNameText.setTextColor(WHITE)
            holder.binding.recommendedText.setTextColor(WHITE)
            holder.binding.transportImage.setColorFilter(WHITE)
            holder.binding.transportImage.setBackgroundColor(TRANSPARENT)
            holder.binding.mainContainer.setBackgroundColor(primaryColor)
        } else {
            holder.binding.lineNameText.setTextColor(BLACK)
            holder.binding.recommendedText.setTextColor(BLACK)
            holder.binding.transportImage.setBackgroundColor(WHITE)
            holder.binding.transportImage.setColorFilter(BLACK)
            holder.binding.mainContainer.setBackgroundColor(WHITE)
        }
//        val imageLoader = ImageLoader.Builder(holder.binding.root.context)
//            .components {
//                add(SvgDecoder.Factory())
//            }
//            .build()
//        val request = ImageRequest.Builder(holder.binding.root.context)
//            .crossfade(true)
//            .crossfade(500)
//            .data(line.categoryRef)
//            .target(holder.binding.transportmage)
//            .build()
//
//        imageLoader.enqueue(request)
        holder.binding.possibleRouteContainer.setOnClickListener {
            lastSelectedPosition = position
            notifyDataSetChanged()
            listener.onCityTap(line)
        }
    }

    override fun getItemCount(): Int {
        return linesList.size
    }

    fun updateList(linesList: MutableList<LinePath>) {
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
