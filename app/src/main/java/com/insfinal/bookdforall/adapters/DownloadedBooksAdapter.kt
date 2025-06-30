package com.insfinal.bookdforall.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R // Pastikan ini benar untuk drawable
import com.insfinal.bookdforall.databinding.ItemBookHorizontalBinding // Asumsi menggunakan layout ini
import com.insfinal.bookdforall.model.DownloadedBook
import com.insfinal.bookdforall.utils.BookAssetPaths // Import BookAssetPaths

class DownloadedBooksAdapter(
    private var books: List<DownloadedBook>,
    private val onItemClick: (DownloadedBook) -> Unit
) : RecyclerView.Adapter<DownloadedBooksAdapter.DownloadedBookViewHolder>() {

    inner class DownloadedBookViewHolder(private val binding: ItemBookHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: DownloadedBook) {
            // Di sini Anda perlu memuat cover jika Anda menyimpannya atau dapat membuatnya berdasarkan buku
            // Karena DownloadedBook hanya menyimpan nama file, kita perlu mencarinya di BookAssetPaths
            val coverFileName = BookAssetPaths.getCoverImageFileName(book.bookId)
            if (coverFileName != null) {
                val assetPath = BookAssetPaths.ASSETS_ROOT + BookAssetPaths.ASSETS_IMG_PATH + coverFileName
                Glide.with(binding.ivBookCover.context)
                    .load(assetPath)
                    .placeholder(R.drawable.placeholder_book_cover)
                    .error(R.drawable.error_book_cover)
                    .into(binding.ivBookCover)
            } else {
                // Fallback jika cover tidak ditemukan di assets
                Glide.with(binding.ivBookCover.context)
                    .load(R.drawable.placeholder_book_cover)
                    .into(binding.ivBookCover)
            }

            binding.tvBookTitle.text = book.title
            binding.tvBookAuthor.text = book.author
            // Tambahkan elemen UI untuk menampilkan halaman terakhir dibaca jika ada
            // Contoh: binding.tvProgress.text = "Halaman ${book.lastReadPage + 1}"

            itemView.setOnClickListener {
                onItemClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedBookViewHolder {
        val binding =
            ItemBookHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DownloadedBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadedBookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    fun updateData(newBooks: List<DownloadedBook>) {
        books = newBooks
        notifyDataSetChanged()
    }
}