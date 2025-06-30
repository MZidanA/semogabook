package com.insfinal.bookdforall.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.insfinal.bookdforall.adapters.BookAdapter
import com.insfinal.bookdforall.databinding.FragmentHomeBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.viewmodel.HomeViewModel

// HomeFragment menampilkan daftar buku yang sedang tren dan buku-buku yang dikategorikan.
class HomeFragment : Fragment() {

    // Variabel binding untuk mengakses elemen UI dari layout fragment_home.xml.
    private var _binding: FragmentHomeBinding? = null
    // Properti praktis untuk mendapatkan _binding tanpa nullable.
    private val binding get() = _binding!!
    // ViewModel untuk mengelola data terkait tampilan beranda.
    private val viewModel: HomeViewModel by viewModels()

    // Dipanggil untuk membuat dan mengembalikan tampilan hirarki yang terkait dengan fragmen.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Mengembang (inflate) layout untuk fragmen ini.
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Dipanggil segera setelah onCreateView() dan memastikan bahwa tampilan fragmen telah dibuat.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listener untuk pencarian.
        binding.search.addTextChangedListener { editable ->
            val query = editable.toString() // Mendapatkan query pencarian.
            viewModel.filterBooks(query) // Memfilter buku berdasarkan query.
        }

        // Mengamati data buku yang sedang tren dari ViewModel.
        viewModel.trendingBooks.observe(viewLifecycleOwner) { books ->
            setupBookRecyclerView(binding.rvTrending, books) // Mengatur RecyclerView untuk buku trending.
            Log.d("HomeFragment", "Trending Books displayed: ${books.size}")
        }

        // Mengamati data buku yang dikategorikan secara dinamis dari ViewModel.
        viewModel.categorizedBooks.observe(viewLifecycleOwner) { categoryMap ->
            displayCategoriesDynamically(categoryMap) // Menampilkan kategori secara dinamis.
        }

        // Mengamati status loading dari ViewModel.
        viewModel.isLoading.observe(viewLifecycleOwner) {
            // Tambahkan UI indikator loading di sini jika diperlukan.
        }

        // Mengamati pesan error dari ViewModel.
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("HomeFragment", it) // Mencatat error ke Logcat.
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show() // Menampilkan Toast error.
            }
        }
    }

    // Mengatur RecyclerView untuk menampilkan daftar buku.
    private fun setupBookRecyclerView(
        recyclerView: RecyclerView,
        books: List<Book>
    ) {
        // Mengatur LayoutManager menjadi LinearLayoutManager horizontal.
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // Mengatur adapter untuk RecyclerView.
        recyclerView.adapter = BookAdapter(books) { clickedBook ->
            val intent = Intent(requireContext(), BookDetailActivity::class.java)
            intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, clickedBook.bookId) // Meneruskan ID buku.
            intent.putExtra("book_object", clickedBook) // Meneruskan objek buku.
            startActivity(intent) // Memulai BookDetailActivity.
        }
    }

    // Menampilkan kategori buku secara dinamis di UI.
    private fun displayCategoriesDynamically(categoryMap: Map<String, List<Book>>) {
        val container = binding.containerKategori // Container untuk kategori.
        container.removeAllViews() // Menghapus semua tampilan yang ada sebelumnya.

        categoryMap.forEach { (kategori, bukuList) ->
            if (bukuList.isEmpty()) return@forEach // Melewati kategori jika daftar bukunya kosong.

            // Judul kategori.
            val tvCategory = TextView(requireContext()).apply {
                text = kategori.replaceFirstChar { it.uppercase() } // Mengubah huruf pertama kategori menjadi kapital.
                setTextColor(Color.BLACK) // Mengatur warna teks.
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f) // Mengatur ukuran teks.
                setTypeface(typeface, Typeface.BOLD) // Mengatur gaya teks menjadi tebal.
                setPadding(dp(16), dp(32), dp(16), dp(8)) // Mengatur padding.
            }

            // RecyclerView untuk kategori.
            val rvCategory = RecyclerView(requireContext()).apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) // LayoutManager horizontal.
                adapter = BookAdapter(bukuList) { clickedBook ->
                    val intent = Intent(requireContext(), BookDetailActivity::class.java)
                    intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, clickedBook.bookId)
                    intent.putExtra("book_object", clickedBook)
                    startActivity(intent)
                }
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(240) // Mengatur tinggi RecyclerView.
                )
                setPadding(dp(16), 0, dp(16), 0) // Mengatur padding.
                clipToPadding = false // Memungkinkan item untuk digambar di luar padding.
            }

            // Menambahkan judul kategori dan RecyclerView ke container.
            container.addView(tvCategory)
            container.addView(rvCategory)
        }
    }

    // Fungsi utilitas untuk mengonversi dp menjadi piksel.
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    // Dipanggil saat tampilan fragmen dihancurkan.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mengatur _binding menjadi null untuk menghindari kebocoran memori.
    }
}