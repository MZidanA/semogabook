package com.insfinal.bookdforall.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.insfinal.bookdforall.databinding.ItemPublisherBinding
import com.insfinal.bookdforall.model.Publisher

class PublisherAdapter(
    private val onEditClick: (Publisher) -> Unit,
    private val onDeleteClick: (Publisher) -> Unit
) : ListAdapter<Publisher, PublisherAdapter.PublisherViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Publisher>() {
            override fun areItemsTheSame(oldItem: Publisher, newItem: Publisher): Boolean =
                oldItem.publisherId == newItem.publisherId

            override fun areContentsTheSame(oldItem: Publisher, newItem: Publisher): Boolean =
                oldItem == newItem
        }
    }

    inner class PublisherViewHolder(val binding: ItemPublisherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(publisher: Publisher) {
            binding.tvNamaPenerbit.text = publisher.nama_penerbit
            binding.tvAlamat.text = publisher.alamat
            binding.tvKontak.text = publisher.kontak

            binding.btnEdit.setOnClickListener { onEditClick(publisher) }
            binding.btnDelete.setOnClickListener { onDeleteClick(publisher) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublisherViewHolder {
        val binding = ItemPublisherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PublisherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PublisherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}