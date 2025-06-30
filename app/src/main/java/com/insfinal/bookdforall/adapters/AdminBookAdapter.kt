package com.insfinal.bookdforall.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.utils.BookAssetPaths

class AdminBookAdapter(
    private var books: MutableList<Book> = mutableListOf(),
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<AdminBookAdapter.AdminBookViewHolder>() {

    inner class AdminBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBookCover: ImageView = itemView.findViewById(R.id.iv_book_cover_admin)
        val tvBookTitle: TextView = itemView.findViewById(R.id.tv_book_title_admin)
        val tvBookAuthor: TextView = itemView.findViewById(R.id.tv_book_author_admin)
        val tvBookCategory: TextView = itemView.findViewById(R.id.tv_book_category_admin)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit_book)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_book_item)

        fun bind(book: Book) {
            tvBookTitle.text = book.judul
            tvBookAuthor.text = book.penulis
            tvBookCategory.text = "Kategori: ${book.kategori}"

            // Gunakan URL absolut jika coverImageUrl tidak null dan tidak kosong
            if (!book.coverImageUrl.isNullOrEmpty()) {
                val fullImageUrl = "http://10.70.4.146:3000" + book.coverImageUrl
                Glide.with(itemView.context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.placeholder_book_cover)
                    .error(R.drawable.error_book_cover)
                    .into(ivBookCover)
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.placeholder_book_cover)
                    .into(ivBookCover)
            }

            btnEdit.setOnClickListener { onEditClick(book) }
            btnDelete.setOnClickListener { onDeleteClick(book) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_book, parent, false)
        return AdminBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminBookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    fun updateData(newBooks: List<Book>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }
}
