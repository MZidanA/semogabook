// AdminBookListFragment.kt
package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.insfinal.bookdforall.databinding.FragmentAdminBookListBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.adapters.AdminBookAdapter
import com.insfinal.bookdforall.viewmodel.AdminBookViewModel
import kotlinx.coroutines.launch

// AdminBookListFragment adalah fragmen untuk mengelola daftar buku oleh admin.
class AdminBookListFragment : Fragment() {

    // Variabel binding untuk mengakses elemen UI dari layout fragment_admin_book_list.xml.
    private var _binding: FragmentAdminBookListBinding? = null
    // Properti praktis untuk mendapatkan _binding tanpa nullable.
    private val binding get() = _binding!!
    // ViewModel untuk mengelola data dan logika bisnis terkait daftar buku admin.
    private val viewModel: AdminBookViewModel by viewModels()

    // Adapter untuk RecyclerView yang menampilkan daftar buku.
    private lateinit var bookAdapter: AdminBookAdapter

    // Dipanggil untuk membuat dan mengembalikan tampilan hirarki yang terkait dengan fragmen.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Mengembang (inflate) layout untuk fragmen ini.
        _binding = FragmentAdminBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Dipanggil segera setelah onCreateView() dan memastikan bahwa tampilan fragmen telah dibuat.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView() // Mengatur RecyclerView.
        observeViewModel() // Mengamati data dari ViewModel.
        viewModel.loadBooks() // Memuat daftar buku saat tampilan dibuat.

        // Mengatur OnClickListener untuk Floating Action Button (FAB) "Tambah Buku".
        binding.fabAddBook.setOnClickListener {
            startActivity(Intent(requireContext(), AddBookActivity::class.java)) // Memulai AddBookActivity.
        }
    }

    // Mengatur RecyclerView dengan adapter dan LayoutManager.
    private fun setupRecyclerView() {
        // Menginisialisasi AdminBookAdapter dengan callback untuk edit dan hapus.
        bookAdapter = AdminBookAdapter(
            onEditClick = { book ->
                val intent = Intent(requireContext(), EditBookActivity::class.java)
                intent.putExtra(EditBookActivity.EXTRA_BOOK_ID, book.bookId) // Meneruskan ID buku yang akan diedit.
                startActivity(intent) // Memulai EditBookActivity.
            },
            onDeleteClick = { book ->
                showDeleteConfirmationDialog(book) // Menampilkan dialog konfirmasi penghapusan.
            }
        )
        binding.rvAdminBooks.apply {
            layoutManager = LinearLayoutManager(requireContext()) // Mengatur LayoutManager menjadi linear vertikal.
            adapter = bookAdapter // Mengatur adapter untuk RecyclerView.
        }
    }

    // Mengamati LiveData dari ViewModel untuk memperbarui UI.
    private fun observeViewModel() {
        // Mengamati daftar buku dari ViewModel.
        viewModel.booksList.observe(viewLifecycleOwner) { books ->
            bookAdapter.updateData(books) // Memperbarui data di adapter.
            // Menampilkan atau menyembunyikan tampilan kosong jika daftar buku kosong.
            binding.tvEmptyState.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
        }

        // Mengamati status loading dari ViewModel.
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE // Menampilkan atau menyembunyikan ProgressBar.
        }

        // Mengamati event sukses dari ViewModel menggunakan Flow.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventSuccess.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show() // Menampilkan Toast sukses.
                viewModel.loadBooks() // Memuat ulang buku setelah operasi sukses.
            }
        }

        // Mengamati event error dari ViewModel menggunakan Flow.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventError.collect { message ->
                Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show() // Menampilkan Toast error.
                Log.e("AdminBookListFragment", "Error: $message") // Mencatat error ke Logcat.
            }
        }
    }

    // Menampilkan dialog konfirmasi penghapusan buku.
    private fun showDeleteConfirmationDialog(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Buku")
            .setMessage("Apakah Anda yakin ingin menghapus buku '${book.judul}'?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteBook(book.bookId) // Memanggil ViewModel untuk menghapus buku.
            }
            .setNegativeButton("Batal", null) // Tidak melakukan apa-apa jika dibatalkan.
            .show()
    }

    // Dipanggil saat fragmen menjadi terlihat kembali oleh pengguna.
    override fun onResume() {
        super.onResume()
        viewModel.loadBooks() // Memuat ulang buku untuk memastikan data terbaru.
    }

    // Dipanggil saat tampilan fragmen dihancurkan.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mengatur _binding menjadi null untuk menghindari kebocoran memori.
    }
}