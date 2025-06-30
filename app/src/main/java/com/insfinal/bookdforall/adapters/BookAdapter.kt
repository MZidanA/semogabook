package com.insfinal.bookdforall.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ItemBookHorizontalBinding
import com.insfinal.bookdforall.model.Book
import com.bumptech.glide.Glide


class BookAdapter(
    private var books: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: ItemBookHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            val imageUrl = book.coverImageUrl

            val fullImageUrl = "http:/10.70.4.146:3000$imageUrl"

            Log.d("BookAdapter", "Image loading: $fullImageUrl")

            Glide.with(itemView.context)
                .load(fullImageUrl)
                .placeholder(R.drawable.placeholder_book_cover)
                .error(R.drawable.error_book_cover)
                .into(binding.ivBookCover)


            binding.tvBookTitle.text = book.judul
            binding.tvBookAuthor.text = book.penulis

            binding.root.setOnClickListener {
                onItemClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookHorizontalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    fun updateData(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}
