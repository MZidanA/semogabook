package com.insfinal.bookdforall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.insfinal.bookdforall.databinding.ActivityAddBookBinding
import com.insfinal.bookdforall.repository.BookRepository
import com.insfinal.bookdforall.utils.FileUtils
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import java.util.Calendar

// Kelas AddBookActivity bertanggung jawab untuk menambahkan buku baru ke sistem.
class AddBookActivity : AppCompatActivity() {

    // Binding untuk mengakses elemen-elemen UI dari layout activity_add_book.xml.
    private lateinit var binding: ActivityAddBookBinding
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
            binding.fileNameText.text = FileUtils.getFileName(this, uri) // Menampilkan nama file PDF yang dipilih.
        }
    }

    // Dipanggil saat aktivitas pertama kali dibuat.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginisialisasi binding dengan mengembang (inflate) layout.
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        // Mengatur tampilan konten aktivitas.
        setContentView(binding.root)

        // Menyembunyikan ActionBar.
        supportActionBar?.hide()
        // Mengatur OnClickListener untuk tombol kembali.
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Menangani tindakan tombol kembali.
        }

        // Mengatur OnClickListener untuk tombol "Unggah Sampul".
        binding.btnUploadCover.setOnClickListener {
            pickImageLauncher.launch("image/*") // Membuka pemilih gambar.
        }

        // Mengatur OnClickListener untuk tombol "Pilih File Buku".
        binding.selectBookFileButton.setOnClickListener {
            pickPdfLauncher.launch("application/pdf") // Membuka pemilih file PDF.
        }

        // Mengatur OnClickListener untuk tombol "Tambahkan Buku".
        binding.btnAddBook.setOnClickListener {
            addBook() // Memanggil fungsi untuk menambahkan buku.
        }
        // Mengatur OnClickListener untuk EditText "Tanggal Rilis" agar menampilkan DatePickerDialog.
        binding.etReleaseDate.setOnClickListener {
            val calendar = Calendar.getInstance() // Mendapatkan instance Kalender saat ini.
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

    // Fungsi untuk menambahkan buku baru ke API.
    private fun addBook() {
        // Mengambil nilai-nilai input dari EditText.
        val judul = binding.etJudul.text.toString().trim()
        val penulis = binding.etPenulis.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val kategori = binding.etKategori.text.toString().trim()
        val harga = binding.etHarga.text.toString().trim().toIntOrNull()
        val format = binding.etFormat.text.toString().trim()
        val jumlahHalaman = binding.etTotalPages.text.toString().trim().toIntOrNull()
        val tanggal = binding.etReleaseDate.text.toString().trim()

        // Validasi apakah semua kolom wajib diisi dan file telah dipilih.
        if (judul.isEmpty() || penulis.isEmpty() || deskripsi.isEmpty() || kategori.isEmpty() ||
            harga == null || format.isEmpty() || jumlahHalaman == null || tanggal.isEmpty() ||
            selectedImageUri == null || selectedPdfUri == null
        ) {
            Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
            return // Menghentikan eksekusi jika ada kolom kosong atau file belum dipilih.
        }

        lifecycleScope.launch {
            try {
                // Memanggil fungsi uploadBook dari BookRepository untuk mengunggah buku baru.
                val response = BookRepository().uploadBook(
                    context = this@AddBookActivity,
                    judul = judul,
                    penulis = penulis,
                    deskripsi = deskripsi,
                    kategori = kategori,
                    harga = harga,
                    format = format,
                    jumlahHalaman = jumlahHalaman,
                    tanggal = tanggal,
                    publisherId = 1, // Publisher ID (bisa diganti menjadi dinamis).
                    contentProviderId = 1, // Content Provider ID (bisa diganti menjadi dinamis).
                    coverUri = selectedImageUri!!, // URI gambar sampul (dijamin tidak null karena validasi).
                    pdfUri = selectedPdfUri!! // URI file PDF (dijamin tidak null karena validasi).
                )

                // Memeriksa apakah permintaan berhasil.
                if (response.isSuccessful) {
                    Toast.makeText(this@AddBookActivity, "Buku berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    finish() // Menutup aktivitas setelah buku berhasil ditambahkan.
                } else {
                    val errorBody = response.errorBody()?.string() // Mendapatkan body error dari respons yang gagal.
                    Toast.makeText(
                        this@AddBookActivity,
                        "Gagal menambahkan buku: ${errorBody ?: response.message()}", // Menampilkan pesan error.
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("AddBookActivity", "Upload error: $errorBody") // Mencatat error ke Logcat.
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddBookActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show() // Menampilkan Toast error.
                Log.e("AddBookActivity", "Exception: ${e.message}", e) // Mencatat exception ke Logcat.
            }
        }
    }
}