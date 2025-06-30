package com.insfinal.bookdforall.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.insfinal.bookdforall.databinding.ItemAdminUserBinding
import com.insfinal.bookdforall.model.User // Using User model

class AdminUserAdapter(
    private val onDeleteClick: (User) -> Unit // Callback receives User object
) : ListAdapter<User, AdminUserAdapter.UserViewHolder>(DIFF_CALLBACK) { // ListAdapter for User

    inner class UserViewHolder(val binding: ItemAdminUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) { // Binds User object
            binding.userName.text = user.nama
            binding.userEmail.text = user.email
            binding.userRole.text = if (user.isAdmin) "Admin" else "Pengguna"
            binding.btnDeleteUser.setOnClickListener { onDeleteClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = currentList.size // Use currentList for ListAdapter

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position)) // Use getItem for ListAdapter
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() { // DiffUtil for User
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.userId == newItem.userId // <--- PERBAIKAN DI SINI: Gunakan userId

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }
    }
}