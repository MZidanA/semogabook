package com.insfinal.bookdforall.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityBookDetailBinding
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.repository.BookRepository
import com.insfinal.bookdforall.utils.BookAssetPaths
import com.insfinal.bookdforall.utils.PrefManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

// BookDetailActivity menampilkan detail lengkap dari sebuah buku dan menangani fitur unduh/baca.
class BookDetailActivity : AppCompatActivity() {

    // Binding untuk mengakses elemen-elemen UI dari layout activity_book_detail.xml.
    private lateinit var binding: ActivityBookDetailBinding
    // Variabel boolean untuk melacak apakah buku sudah diunduh.
    private var isBookDownloaded: Boolean = false
    // Handler untuk menunda eksekusi kode (misalnya untuk simulasi progress bar).
    private val handler = Handler(Looper.getMainLooper())
    // Variabel untuk melacak progress unduhan simulasi.
    private var downloadProgress: Int = 0
    // Objek buku yang sedang ditampilkan detailnya.
    private var book: Book? = null

    // Launcher untuk memulai PdfViewerActivity dan menerima hasilnya.
    private lateinit var openPdfViewerLauncher: ActivityResultLauncher<Intent>

    // Dipanggil saat aktivitas pertama kali dibuat.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginisialisasi binding dengan mengembang (inflate) layout.
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        // Mengatur tampilan konten aktivitas.
        setContentView(binding.root)

        // Menginisialisasi ActivityResultLauncher untuk PdfViewerActivity.
        openPdfViewerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("BookDetailActivity", "PdfViewerActivity finished. Bookmark saved (handled by PdfViewerActivity).")
            }
            // Setelah kembali dari PdfViewerActivity, perbarui status tombol.
            book?.let { currentBook ->
                isBookDownloaded = isBookDownloaded(currentBook) // Periksa ulang status unduhan.
                updateButtonState(currentBook) // Perbarui tampilan tombol.
            }
        }

        // Mendapatkan ID buku dari Intent.
        val bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)
        // Memeriksa apakah ID buku valid.
        if (bookId != -1) {
            // Mencoba mendapatkan objek buku langsung dari Intent (jika diteruskan).
            book = IntentCompat.getParcelableExtra(intent, "book_object", Book::class.java)
            if (book != null) {
                displayBookDetails(book!!) // Menampilkan detail buku jika objek tersedia.
                isBookDownloaded = isBookDownloaded(book!!) // Periksa status unduhan.
                updateButtonState(book!!) // Perbarui tampilan tombol.
            } else {
                fetchBookById(bookId) // Jika objek buku tidak ada, ambil detailnya dari API.
            }
        } else {
            showError("ID buku tidak valid.") // Tampilkan error jika ID tidak valid.
        }

        // Mengatur OnClickListener untuk tombol "Kembali".
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Menangani tindakan tombol kembali.
        }

        // Mengatur OnClickListener untuk tombol "Unduh".
        binding.btnDownload.setOnClickListener {
            book?.let {
                val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
                val fileName = it.pdfUrl
                if (fileName != null) {
                    val localFile = File(downloadsDir, fileName)
                    // Jika file sudah diunduh, tampilkan pesan dan perbarui status.
                    if (localFile.exists() && localFile.length() > 0) {
                        Toast.makeText(this, "${it.judul} sudah diunduh.", Toast.LENGTH_SHORT).show()
                        isBookDownloaded = true
                        updateButtonState(it)
                    } else {
                        simulateAndCopyDownload(it) // Mulai simulasi unduhan.
                    }
                } else {
                    Toast.makeText(this, "File PDF untuk buku ini tidak terdefinisi.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mengatur OnClickListener untuk tombol "Baca Pratinjau".
        binding.btnBacaPratinjau.setOnClickListener {
            Toast.makeText(this, "Membuka Pratinjau untuk: ${book?.judul}", Toast.LENGTH_SHORT).show()
            book?.let { openBookPdf(it) } // Buka PDF buku untuk pratinjau.
        }

        // Mengatur OnClickListener untuk tombol "Baca".
        binding.btnBacaSingle.setOnClickListener {
            book?.let { currentBook ->
                val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
                val fileName = currentBook.pdfUrl
                if (fileName != null) {
                    val file = File(downloadsDir, fileName)
                    // Jika file ada dan tidak kosong, buka PdfViewerActivity.
                    if (file.exists() && file.length() > 0) {
                        val intent = Intent(this, PdfViewerActivity::class.java).apply {
                            putExtra(PdfViewerActivity.EXTRA_BOOK_ID, currentBook.bookId)
                            putExtra(PdfViewerActivity.EXTRA_BOOK_FILENAME, fileName)
                            putExtra(PdfViewerActivity.EXTRA_BOOK_TITLE, currentBook.judul)
                        }
                        openPdfViewerLauncher.launch(intent)
                    } else {
                        // Tampilkan pesan error jika file tidak ditemukan atau kosong.
                        val errorMessage = if (file.exists() && file.length() == 0L) {
                            "File buku ditemukan tapi kosong. Harap unduh ulang."
                        } else {
                            "File buku tidak ditemukan. Harap unduh terlebih dahulu."
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        isBookDownloaded = false
                        updateButtonState(currentBook)
                    }
                } else {
                    Toast.makeText(this, "File PDF untuk buku ini tidak terdefinisi.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mengatur OnClickListener untuk tombol "Tulis Ulasan".
        binding.btnWriteReview.setOnClickListener {
            Toast.makeText(this, "Menulis ulasan untuk: ${book?.judul}", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mengambil detail buku dari API berdasarkan ID.
    private fun fetchBookById(bookId: Int) {
        lifecycleScope.launch {
            try {
                val response = BookRepository().getBookById(bookId) // Memanggil API untuk mendapatkan buku.
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        book = result // Menyimpan objek buku yang diambil.
                        displayBookDetails(book!!) // Menampilkan detail buku.
                        isBookDownloaded = isBookDownloaded(book!!) // Periksa status unduhan.
                        updateButtonState(book!!) // Perbarui tampilan tombol.
                    } else {
                        showError("Buku tidak ditemukan.")
                    }
                } else {
                    showError("Gagal memuat buku. Kode: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    // Fungsi untuk menampilkan detail buku ke elemen UI.
    private fun displayBookDetails(book: Book) {
        val coverUrl = book.coverImageUrl
        // Membangun URL gambar lengkap.
        val fullImageUrl = if (!coverUrl.isNullOrEmpty() && coverUrl.startsWith("http")) coverUrl else "http://10.70.4.146:3000$coverUrl"
        // Memuat gambar sampul menggunakan Glide.
        Glide.with(this)
            .load(fullImageUrl)
            .placeholder(R.drawable.placeholder_book_cover) // Placeholder saat memuat.
            .error(R.drawable.error_book_cover) // Gambar error jika gagal.
            .into(binding.ivBookCoverDetail)

        binding.tvBookTitleDetail.text = book.judul
        binding.tvBookAuthorDetail.text = book.penulis
        binding.tvBookDescription.text = book.deskripsi
        binding.ratingBar.rating = book.rating ?: 0f
        binding.tvRatingCount.text = "(${book.ratingCount ?: 0})"
        binding.tvInfoLanguage.text = "Indonesia"
        binding.tvInfoReleaseDate.text = book.tanggal ?: "-"
        binding.tvInfoPublisher.text = book.publisherId?.toString() ?: "N/A"
        binding.tvInfoAuthor.text = book.penulis
        binding.tvInfoPages.text = "${book.jumlahHalaman ?: "?"} Halaman"
        binding.tvInfoCategory.text = book.kategori
        binding.tvInfoFormat.text = book.format.ifBlank { "-" }
    }

    // Fungsi untuk memeriksa apakah buku sudah diunduh.
    private fun isBookDownloaded(book: Book): Boolean {
        val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
        val file = File(downloadsDir, book.pdfUrl ?: return false) // Mendapatkan objek File untuk PDF.
        return file.exists() && file.length() > 0 // Mengembalikan true jika file ada dan tidak kosong.
    }

    // Fungsi untuk mengunduh file PDF dari URL.
    private fun downloadPdfFromUrl(book: Book, onComplete: (Boolean) -> Unit) {
        val fileName = book.pdfUrl ?: return onComplete(false) // Mendapatkan nama file PDF.
        val downloadDir = File(getExternalFilesDir(null), "books_downloaded")

        // Cek dan buat subfolder 'uploads/pdf' jika ada.
        val fullPath = File(downloadDir, fileName)
        val parentDir = fullPath.parentFile
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs() // ⬅️ BUAT SEMUA FOLDER YANG DIBUTUHKAN.
        }

        // Membangun URL lengkap untuk unduhan.
        val fullUrl = if (fileName.startsWith("http")) fileName else "http://10.70.4.146:3000$fileName"

        lifecycleScope.launch(Dispatchers.IO) { // Meluncurkan coroutine di Dispatcher.IO untuk operasi jaringan/file.
            try {
                val url = URL(fullUrl)
                val connection = url.openConnection()
                connection.connect()

                val input = url.openStream() // Membuka stream input dari URL.
                val output = FileOutputStream(fullPath) // Membuka stream output ke file lokal.

                val buffer = ByteArray(4096)
                var bytesRead: Int
                // Membaca dari input dan menulis ke output.
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }

                output.flush() // Memastikan semua data ditulis.
                output.close() // Menutup stream output.
                input.close() // Menutup stream input.

                withContext(Dispatchers.Main) {
                    onComplete(true) // Memanggil callback sukses di thread utama.
                }
            } catch (e: Exception) {
                e.printStackTrace() // Mencetak stack trace jika terjadi error.
                withContext(Dispatchers.Main) {
                    onComplete(false) // Memanggil callback gagal di thread utama.
                }
            }
        }
    }

    // Fungsi untuk mensimulasikan unduhan dan menyalin file.
    private fun simulateAndCopyDownload(book: Book) {
        // Menerapkan transisi Fade untuk perubahan tampilan tombol.
        TransitionManager.beginDelayedTransition(binding.bottomButtonContainer, Fade())
        binding.layoutTwoButtons.visibility = View.GONE // Sembunyikan dua tombol.
        binding.btnBacaSingle.visibility = View.GONE // Sembunyikan tombol baca tunggal.
        binding.progressBarDownload.visibility = View.VISIBLE // Tampilkan progress bar.
        binding.progressBarDownload.progress = 0 // Atur progress ke 0.

        downloadProgress = 0
        handler.post(object : Runnable {
            override fun run() {
                if (downloadProgress < 100) {
                    downloadProgress += 10 // Tingkatkan progress.
                    binding.progressBarDownload.progress = downloadProgress // Perbarui progress bar.
                    handler.postDelayed(this, 200) // Tunda eksekusi selanjutnya.
                } else {
                    // Ganti dari copy asset → download dari URL.
                    downloadPdfFromUrl(book) { success ->
                        if (success) {
                            isBookDownloaded = true // Set status unduhan ke true.
                            PrefManager.addDownloadedBook(this@BookDetailActivity, book.bookId, book.pdfUrl ?: "", book.judul, book.penulis) // Tambahkan buku ke daftar buku yang diunduh.
                            updateButtonState(book) // Perbarui tampilan tombol.
                            Toast.makeText(this@BookDetailActivity, "${book.judul} berhasil diunduh!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@BookDetailActivity, "Gagal mengunduh buku ${book.judul}.", Toast.LENGTH_LONG).show()
                            isBookDownloaded = false // Set status unduhan ke false.
                            updateButtonState(book) // Perbarui tampilan tombol.
                        }
                    }
                }
            }
        })
    }

    // Fungsi untuk memperbarui keadaan tombol berdasarkan status unduhan buku.
    private fun updateButtonState(book: Book?) {
        TransitionManager.beginDelayedTransition(binding.detailRootLayout, Fade()) // Menerapkan transisi.
        isBookDownloaded = isBookDownloaded(book ?: return) // Periksa status unduhan.
        if (isBookDownloaded) {
            binding.layoutTwoButtons.visibility = View.GONE // Sembunyikan dua tombol.
            binding.progressBarDownload.visibility = View.GONE // Sembunyikan progress bar.
            binding.btnBacaSingle.visibility = View.VISIBLE // Tampilkan tombol baca tunggal.
            binding.btnBacaSingle.isEnabled = true // Aktifkan tombol baca tunggal.
        } else {
            binding.btnBacaSingle.visibility = View.GONE // Sembunyikan tombol baca tunggal.
            binding.progressBarDownload.visibility = View.GONE // Sembunyikan progress bar.
            binding.layoutTwoButtons.visibility = View.VISIBLE // Tampilkan dua tombol.
            binding.btnDownload.isEnabled = true // Aktifkan tombol unduh.
            binding.btnBacaPratinjau.isEnabled = true // Aktifkan tombol baca pratinjau.
        }
    }

    // Fungsi untuk menampilkan Toast error dan menutup aktivitas.
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish() // Menutup aktivitas.
    }

    // Dipanggil saat aktivitas menjadi terlihat oleh pengguna.
    override fun onStart() {
        super.onStart()
        book?.let {
            isBookDownloaded = isBookDownloaded(it) // Perbarui status unduhan.
            updateButtonState(it) // Perbarui tampilan tombol.
        }
    }

    // Dipanggil saat aktivitas dihancurkan.
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Menghapus semua callback dan pesan dari handler.
    }

    // Objek pendamping untuk menyimpan konstanta.
    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id" // Kunci untuk meneruskan ID buku melalui Intent.
    }

    // Dipanggil untuk menyimpan status UI aktivitas.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("is_downloaded", isBookDownloaded) // Menyimpan status unduhan.
        outState.putParcelable("current_book", book) // Menyimpan objek buku.
    }

    // Dipanggil untuk memulihkan status UI aktivitas.
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isBookDownloaded = savedInstanceState.getBoolean("is_downloaded", false) // Memulihkan status unduhan.
        book = BundleCompat.getParcelable(savedInstanceState, "current_book", Book::class.java) // Memulihkan objek buku.
    }

    // Fungsi untuk membuka file PDF buku.
    private fun openBookPdf(book: Book) {
        val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
        val assetFileName = book.pdfUrl ?: return Toast.makeText(this, "Aset PDF buku tidak ditemukan.", Toast.LENGTH_SHORT).show()
        val pdfFile = File(downloadsDir, assetFileName)
        // Jika file PDF ada dan tidak kosong, buka PdfViewerActivity.
        if (pdfFile.exists() && pdfFile.length() > 0) {
            val intent = Intent(this, PdfViewerActivity::class.java).apply {
                putExtra(PdfViewerActivity.EXTRA_BOOK_ID, book.bookId)
                putExtra(PdfViewerActivity.EXTRA_BOOK_FILENAME, assetFileName)
                putExtra(PdfViewerActivity.EXTRA_BOOK_TITLE, book.judul)
            }
            openPdfViewerLauncher.launch(intent)
        } else {
            Toast.makeText(this, "File is not downloaded or is empty. Please download first.", Toast.LENGTH_SHORT).show()
        }
    }
}