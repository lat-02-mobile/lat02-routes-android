package com.jalasoft.routesapp.util

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeGesture(context: Context, swipeDirs: Int = (ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)) : ItemTouchHelper.SimpleCallback(0, swipeDirs) {

    private val deleteColor = ContextCompat.getColor(context, R.color.delete_color)
    private val editColor = ContextCompat.getColor(context, R.color.selected_point_color)
    private val deleteIcon = R.drawable.ic_baseline_delete_24
    private val editIcon = R.drawable.ic_baseline_edit_24

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftBackgroundColor(deleteColor)
            .addSwipeLeftActionIcon(deleteIcon)
            .addSwipeRightBackgroundColor(editColor)
            .addSwipeRightActionIcon(editIcon)
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
