// EditBookActivity.kt
package com.insfinal.bookdforall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityEditBookBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.model.CreateBookRequest
import com.insfinal.bookdforall.repository.BookRepository
import com.insfinal.bookdforall.utils.BookAssetPaths
import com.insfinal.bookdforall.utils.FileUtils
import kotlinx.coroutines.launch
import java.io.File
import android.app.DatePickerDialog
import java.util.Calendar

// Kelas EditBookActivity bertanggung jawab untuk mengedit detail buku yang sudah ada.
class EditBookActivity : AppCompatActivity() {

    // Binding untuk mengakses elemen-elemen UI dari layout activity_edit_book.xml.
    private lateinit var binding: ActivityEditBookBinding
    // ID buku yang akan diedit, diinisialisasi dengan nilai default -1.
    private var bookId: Int = -1
    // Objek buku saat ini yang sedang diedit.
    private var currentBook: Book? = null
    // URI untuk gambar sampul yang dipilih dari galeri.
    private var selectedImageUri: Uri? = null
    // URI untuk file PDF buku yang dipilih dari penyimpanan.
    private var selectedPdfUri: Uri? = null

    // Launcher untuk memilih gambar sampul dari penyimpanan.
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it // Menyimpan URI gambar yang dipilih.
            binding.ivCoverPreview.setImageURI(it) // Menampilkan gambar di ImageView.
        }
    }

    // Launcher untuk memilih file PDF dari penyimpanan.
    private val pickPdfLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedPdfUri = it // Menyimpan URI PDF yang dipilih.
            // Menampilkan nama file PDF yang dipilih, atau URI terakhir sebagai fallback.
            binding.fileNameText.text = FileUtils.getFileName(this, uri) ?: uri.lastPathSegment ?: "File dipilih"
        }
    }

    // Dipanggil saat aktivitas pertama kali dibuat.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginisialisasi binding dengan mengembang (inflate) layout.
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        // Mengatur tampilan konten aktivitas.
        setContentView(binding.root)

        // Menyembunyikan ActionBar.
        supportActionBar?.hide()
        // Mengatur OnClickListener untuk tombol kembali.
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Menangani tindakan tombol kembali.
        }

        // Mendapatkan ID buku dari intent.
        bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)
        // Memeriksa apakah ID buku valid.
        if (bookId != -1) {
            fetchBookDetails(bookId) // Mengambil detail buku jika ID valid.
        } else {
            Toast.makeText(this, "ID Buku tidak valid untuk diedit.", Toast.LENGTH_SHORT).show()
            finish() // Menutup aktivitas jika ID tidak valid.
        }

        // Mengatur OnClickListener untuk tombol "Unggah Sampul".
        binding.btnUploadCover.setOnClickListener {
            pickImageLauncher.launch("image/*") // Membuka pemilih gambar.
        }

        // Mengatur OnClickListener untuk tombol "Pilih File Buku".
        binding.selectBookFileButton.setOnClickListener {
            pickPdfLauncher.launch("application/pdf") // Membuka pemilih file PDF.
        }

        // Mengatur OnClickListener untuk tombol "Simpan Perubahan".
        binding.btnSaveBook.setOnClickListener {
            saveBookChanges() // Menyimpan perubahan buku.
        }

        // Mengatur OnClickListener untuk tombol "Hapus Buku".
        binding.btnDeleteBook.setOnClickListener {
            showDeleteConfirmationDialog() // Menampilkan dialog konfirmasi penghapusan.
        }
        // Mengatur OnClickListener untuk EditText "Tanggal Rilis" agar menampilkan DatePickerDialog.
        binding.etReleaseDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            // Mencoba mengurai tanggal dari EditText jika sudah ada nilainya.
            val parts = binding.etReleaseDate.text.toString().split("-")
            if (parts.size == 3) {
                try {
                    // Mengatur kalender ke tanggal yang sudah ada.
                    calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                } catch (_: Exception) {
                    // Mengabaikan exception jika format tanggal tidak valid.
                }
            }

            // Membuat dan menampilkan DatePickerDialog.
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Memformat tanggal yang dipilih ke format YYYY-MM-DD.
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.etReleaseDate.setText(selectedDate) // Mengatur teks EditText dengan tanggal yang dipilih.
                },
                calendar.get(Calendar.YEAR), // Tahun default.
                calendar.get(Calendar.MONTH), // Bulan default.
                calendar.get(Calendar.DAY_OF_MONTH) // Hari default.
            )
            datePicker.show()
        }
    }

    // Fungsi untuk mengambil detail buku dari API berdasarkan ID.
    private fun fetchBookDetails(id: Int) {
        lifecycleScope.launch {
            try {
                val response = BookRepository().getBookById(id) // Memanggil API untuk mendapatkan buku berdasarkan ID.
                if (response.isSuccessful) {
                    response.body()?.let { book ->
                        currentBook = book // Menyimpan buku yang diambil sebagai currentBook.
                        displayBookDetails(book) // Menampilkan detail buku di UI.
                    }
                } else {
                    Toast.makeText(this@EditBookActivity, "Gagal memuat detail buku.", Toast.LENGTH_SHORT).show()
                    Log.e("EditBookActivity", "Failed to fetch book details: ${response.code()}")
                    finish() // Menutup aktivitas jika gagal memuat detail buku.
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditBookActivity, "Error memuat detail buku: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditBookActivity", "Exception fetching book details: ${e.message}", e)
                finish() // Menutup aktivitas jika terjadi exception.
            }
        }
    }

    // Fungsi untuk menampilkan detail buku yang diambil ke elemen UI.
    private fun displayBookDetails(book: Book) {
        binding.etJudul.setText(book.judul)
        binding.etPenulis.setText(book.penulis)
        binding.etDeskripsi.setText(book.deskripsi)
        binding.etKategori.setText(book.kategori)
        binding.etHarga.setText(book.harga.toString())
        binding.etFormat.setText(book.format)
        binding.etReleaseDate.setText(book.tanggal)
        binding.etTotalPages.setText(book.jumlahHalaman.toString())

        // Menentukan lokasi file PDF lokal yang diunduh.
        val downloadsDir = getExternalFilesDir(null)?.let { File(it, "books_downloaded") }
        val localPdfFile = downloadsDir?.let { File(it, File(book.pdfUrl ?: "").name) }

        // Memeriksa keberadaan file PDF lokal.
        if (localPdfFile?.exists() == true && localPdfFile.length() > 0) {
            binding.fileNameText.text = localPdfFile.name // Menampilkan nama file lokal.
            selectedPdfUri = Uri.fromFile(localPdfFile) // Mengatur URI file lokal.
        } else if (!book.pdfUrl.isNullOrEmpty()) {
            binding.fileNameText.text = book.pdfUrl // Menampilkan URL PDF jika tidak ada file lokal.
        } else {
            binding.fileNameText.text = "Belum ada file dipilih" // Menampilkan pesan jika tidak ada file/URL.
            selectedPdfUri = null // Mengatur URI PDF menjadi null.
        }
        val imageUrl = book.coverImageUrl // Mendapatkan URL gambar sampul.

        // Membuat URL gambar sampul lengkap.
        val fullImageUrl = "http://10.70.4.146:3000$imageUrl"

        Log.d("EditBook", "Image loading: $fullImageUrl")
        // Memuat gambar sampul menggunakan Glide jika URL tidak kosong.
        if (!book.coverImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(fullImageUrl)
                .placeholder(R.drawable.placeholder_book_cover) // Placeholder jika gambar sedang dimuat.
                .error(R.drawable.error_book_cover) // Gambar error jika gagal memuat.
                .into(binding.ivCoverPreview) // Menampilkan gambar di ImageView.
        }
    }

    // Fungsi untuk menyimpan perubahan buku ke API.
    private fun saveBookChanges() {
        // Mengambil nilai-nilai yang diedit dari EditText.
        val updatedJudul = binding.etJudul.text.toString().trim()
        val updatedPenulis = binding.etPenulis.text.toString().trim()
        val updatedDeskripsi = binding.etDeskripsi.text.toString().trim()
        val updatedKategori = binding.etKategori.text.toString().trim()
        val updatedHarga = binding.etHarga.text.toString().trim().toIntOrNull()
        val updatedFormat = binding.etFormat.text.toString().trim()
        val updatedReleaseDate = binding.etReleaseDate.text.toString().trim()
        val updatedTotalPages = binding.etTotalPages.text.toString().trim().toIntOrNull()

        // Validasi input wajib.
        if (updatedJudul.isEmpty() || updatedPenulis.isEmpty() || updatedDeskripsi.isEmpty() || updatedKategori.isEmpty() ||
            updatedHarga == null || updatedFormat.isEmpty() || updatedReleaseDate.isEmpty() || updatedTotalPages == null
        ) {
            Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
            return // Menghentikan eksekusi jika ada kolom kosong.
        }

        lifecycleScope.launch {
            try {
                // Memanggil fungsi UpdateuploadBook dari BookRepository untuk memperbarui buku.
                val uploadResponse = BookRepository().UpdateuploadBook(
                    context = this@EditBookActivity,
                    judul = updatedJudul,
                    penulis = updatedPenulis,
                    deskripsi = updatedDeskripsi,
                    kategori = updatedKategori,
                    harga = updatedHarga,
                    format = updatedFormat,
                    jumlahHalaman = updatedTotalPages,
                    tanggal = updatedReleaseDate,
                    publisherId = currentBook?.publisherId ?: 1, // Menggunakan publisherId dari buku saat ini atau default 1.
                    contentProviderId = currentBook?.contentProviderId ?: 1, // Menggunakan contentProviderId dari buku saat ini atau default 1.
                    coverUri = selectedImageUri, // URI gambar sampul yang baru (bisa null).
                    pdfUri = selectedPdfUri // URI file PDF yang baru (bisa null).
                )

                // Memeriksa apakah permintaan pembaruan berhasil.
                if (!uploadResponse.isSuccessful) {
                    val errorBody = uploadResponse.errorBody()?.string()
                    Toast.makeText(this@EditBookActivity, "Metadata disimpan, tapi gagal upload file: ${errorBody ?: uploadResponse.message()}", Toast.LENGTH_LONG).show()
                    return@launch // Menghentikan eksekusi jika unggah file gagal.
                }

                Toast.makeText(this@EditBookActivity, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish() // Menutup aktivitas setelah perubahan berhasil disimpan.

            } catch (e: Exception) {
                Toast.makeText(this@EditBookActivity, "Error saat menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditBookActivity", "Exception saving book: ${e.message}", e)
            }
        }
    }

    // Fungsi untuk menampilkan dialog konfirmasi penghapusan buku.
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Buku")
            .setMessage("Apakah Anda yakin ingin menghapus buku '${currentBook?.judul}'?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteBook() // Memanggil fungsi untuk menghapus buku jika dikonfirmasi.
            }
            .setNegativeButton("Batal", null) // Tidak melakukan apa-apa jika dibatalkan.
            .show()
    }

    // Fungsi untuk menghapus buku dari API.
    private fun deleteBook() {
        lifecycleScope.launch {
            try {
                val response = BookRepository().delete(bookId) // Memanggil API untuk menghapus buku.
                if (response.isSuccessful) {
                    Toast.makeText(this@EditBookActivity, "Buku berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    finish() // Menutup aktivitas setelah buku berhasil dihapus.
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@EditBookActivity, "Gagal menghapus buku: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                    Log.e("EditBookActivity", "Failed to delete book: ${response.code()} - ${errorBody ?: response.message()}")
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditBookActivity, "Error menghapus buku: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("EditBookActivity", "Exception deleting book: ${e.message}", e)
            }
        }
    }

    // Objek pendamping untuk menyimpan konstanta.
    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id" // Kunci untuk meneruskan ID buku melalui Intent.
    }
}