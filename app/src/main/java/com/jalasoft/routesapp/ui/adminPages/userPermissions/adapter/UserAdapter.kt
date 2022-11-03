package com.jalasoft.routesapp.ui.adminPages.userPermissions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.databinding.UserAdminItemBinding

class UserAdapter(var usersList: MutableList<User>, val listener: IUserListener) : RecyclerView.Adapter<UserAdapter.UserViewHolder> () {
    interface IUserListener {
        fun addRevokeUserPermission(user: User)
    }

    class UserViewHolder(val binding: UserAdminItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserAdminItemBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.binding.userItem = usersList[position]
        holder.binding.userAdminContainer.setOnClickListener {
            listener.addRevokeUserPermission(usersList[position])
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    fun updateList(usersList: MutableList<User>) {
        val oldSize = this.usersList.size
        this.usersList = usersList
        if (usersList.size <= oldSize) {
            notifyItemRangeRemoved(0, oldSize)
            notifyItemRangeInserted(0, usersList.size)
        } else {
            notifyItemRangeChanged(0, this.usersList.size)
        }
    }
}