package com.jalasoft.routesapp.ui.adminPages.lines.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.data.model.remote.LineAux
import com.jalasoft.routesapp.databinding.LineAdminItemBinding

class LinesAdminAdapter(var linesList: MutableList<LineAux>, val listener: ILinesAdminListener) : RecyclerView.Adapter<LinesAdminAdapter.LinesAdminViewHolder> () {
    interface ILinesAdminListener {
        fun gotoEditLine(lineAux: LineAux, position: Int)
    }

    class LinesAdminViewHolder(val binding: LineAdminItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinesAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LineAdminItemBinding.inflate(inflater, parent, false)
        return LinesAdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinesAdminViewHolder, position: Int) {
        holder.binding.lineItem = linesList[position]
        holder.binding.lineAdminContainer.setOnClickListener {
            listener.gotoEditLine(linesList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return linesList.size
    }

    fun updateList(linesList: MutableList<LineAux>) {
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
