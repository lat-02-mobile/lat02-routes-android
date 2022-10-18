package com.jalasoft.routesapp.ui.favorites.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity
import com.jalasoft.routesapp.databinding.FavoriteItemBinding
import com.jalasoft.routesapp.util.helpers.DateHelper

class FavoriteAdapter(var favoritesList: MutableList<FavoriteDestinationEntity>, val listener: IFavoriteListener) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    interface IFavoriteListener {
        fun onFavoriteTap(fav: FavoriteDestinationEntity)
    }

    class FavoriteViewHolder(val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavoriteItemBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.binding.favoriteItem = favoritesList[position]
        holder.binding.tvDate.text = holder.binding.root.context.getString(R.string.fav_dest_created_at, DateHelper.convertDoubleToTime(favoritesList[position].createdAt))
        holder.binding.favContainer.setOnClickListener {
            listener.onFavoriteTap(favoritesList[position])
        }
    }

    override fun getItemCount(): Int {
        return favoritesList.size
    }

    fun updateList(favoritesList: MutableList<FavoriteDestinationEntity>) {
        val oldSize = this.favoritesList.size
        this.favoritesList = favoritesList
        if (favoritesList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, favoritesList.size)
        } else {
            notifyItemRangeChanged(0, this.favoritesList.size)
        }
    }
}
