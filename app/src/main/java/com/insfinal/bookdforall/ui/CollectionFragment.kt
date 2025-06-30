package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.insfinal.bookdforall.databinding.FragmentCollectionBinding
import com.insfinal.bookdforall.adapters.DownloadedBooksAdapter
import com.insfinal.bookdforall.model.DownloadedBook
import com.insfinal.bookdforall.utils.PrefManager
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import android.util.Log

// CollectionFragment menampilkan daftar buku yang telah diunduh oleh pengguna.
class CollectionFragment : Fragment() {

    // Variabel binding untuk mengakses elemen UI dari layout fragment_collection.xml.
    // _binding digunakan untuk memastikan akses yang aman ke binding (null safety).
    private var _binding: FragmentCollectionBinding? = null
    // Properti praktis untuk mendapatkan _binding tanpa nullable.
    private val binding get() = _binding!!
    // Adapter untuk RecyclerView yang menampilkan daftar buku yang diunduh.
    private lateinit var adapter: DownloadedBooksAdapter
    // Daftar lengkap semua buku yang diunduh, digunakan untuk filtering.
    private var allDownloadedBooks: List<DownloadedBook> = emptyList()

    // Dipanggil untuk membuat dan mengembalikan tampilan hirarki yang terkait dengan fragmen.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Mengembang (inflate) layout untuk fragmen ini.
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Dipanggil segera setelah onCreateView() dan memastikan bahwa tampilan fragmen telah dibuat.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengatur LayoutManager untuk RecyclerView agar item ditampilkan secara linear vertikal.
        binding.myBooksRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Memuat dan menampilkan buku-buku yang diunduh.
        loadAndDisplayBooks()

        // Menambahkan listener untuk perubahan teks pada SearchView.
        binding.searchView.addTextChangedListener { editable ->
            val query = editable.toString() // Mendapatkan query pencarian.
            // Melakukan filtering pada daftar buku yang diunduh berdasarkan judul atau penulis.
            val filtered = allDownloadedBooks.filter {
                // it.title dan it.author sekarang non-nullable String
                it.title.contains(query, ignoreCase = true) || // Mencocokkan judul (case-insensitive).
                        it.author.contains(query, ignoreCase = true) // Mencocokkan penulis (case-insensitive).
            }
            adapter.updateData(filtered) // Memperbarui data di adapter dengan hasil filter.
            // Menampilkan atau menyembunyikan tampilan kosong berdasarkan hasil filter.
            binding.emptyMyBooksView.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    // Fungsi untuk memuat buku-buku yang diunduh dari PrefManager dan menampilkannya.
    private fun loadAndDisplayBooks() {
        allDownloadedBooks = PrefManager.getDownloadedBooks(requireContext()) // Mendapatkan daftar buku yang diunduh.
        Log.d("CollectionFragment", "Loaded ${allDownloadedBooks.size} downloaded books.")
        allDownloadedBooks.forEach {
            Log.d("CollectionFragment", "Downloaded Book: ${it.title} (${it.fileName})")
        }
        // Menampilkan atau menyembunyikan tampilan kosong jika tidak ada buku yang diunduh.
        binding.emptyMyBooksView.visibility = if (allDownloadedBooks.isEmpty()) View.VISIBLE else View.GONE

        // Menginisialisasi adapter dengan daftar buku dan OnClickListener untuk setiap item.
        adapter = DownloadedBooksAdapter(allDownloadedBooks) { book ->
            // Mendapatkan direktori tempat buku-buku diunduh.
            val downloadsDir = File(requireContext().getExternalFilesDir(null), "books_downloaded")
            // Membuat objek File untuk file PDF buku yang diunduh.
            val pdfFile = File(downloadsDir, book.fileName)

            // Memeriksa apakah file PDF ada dan dapat dibaca.
            if (pdfFile.exists()) {
                val intent = Intent(requireContext(), PdfViewerActivity::class.java)
                intent.putExtra(PdfViewerActivity.EXTRA_BOOK_ID, book.bookId)
                intent.putExtra(PdfViewerActivity.EXTRA_BOOK_FILENAME, book.fileName)
                intent.putExtra(PdfViewerActivity.EXTRA_BOOK_TITLE, book.title)
                startActivity(intent) // Memulai PdfViewerActivity untuk membaca buku.
            } else {
                Toast.makeText(requireContext(), "File buku tidak ditemukan: ${book.fileName}", Toast.LENGTH_SHORT).show()
                // Menghapus entri buku yang diunduh jika file tidak ditemukan.
                PrefManager.removeDownloadedBook(requireContext(), book.bookId)
                loadAndDisplayBooks() // Memuat ulang daftar buku.
            }
        }
        binding.myBooksRecyclerView.adapter = adapter // Mengatur adapter untuk RecyclerView.
    }

    // Dipanggil saat fragmen menjadi terlihat kembali oleh pengguna.
    override fun onResume() {
        super.onResume()
        loadAndDisplayBooks() // Memuat ulang dan menampilkan buku-buku untuk memastikan data terbaru.
    }

    // Dipanggil saat tampilan fragmen dihancurkan.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mengatur _binding menjadi null untuk menghindari kebocoran memori.
    }
}