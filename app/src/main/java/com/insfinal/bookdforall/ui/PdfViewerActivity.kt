package com.insfinal.bookdforall.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityPdfViewerBinding
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.insfinal.bookdforall.utils.PrefManager
import java.io.File

class PdfViewerActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener {

    private lateinit var binding: ActivityPdfViewerBinding
    private var bookId: Int = -1
    private var bookFileName: String // <--- Ubah menjadi String (non-nullable)
    private var bookTitle: String // <--- Ubah menjadi String (non-nullable)
    private var currentPage: Int = 0

    init {
        // Inisialisasi awal properti non-nullable dengan nilai default sementara
        // Mereka akan di-assign ulang di onCreate dari Intent
        bookFileName = ""
        bookTitle = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)
        val receivedFileName = intent.getStringExtra(EXTRA_BOOK_FILENAME)
        val receivedTitle = intent.getStringExtra(EXTRA_BOOK_TITLE)

        // Lakukan pengecekan null di awal dan assign ke properti non-nullable
        if (bookId == -1 || receivedFileName == null || receivedTitle == null) {
            Toast.makeText(this, "Informasi buku tidak valid.", Toast.LENGTH_SHORT).show()
            Log.e("PdfViewerDebug", "Invalid book info: ID, FileName, or Title is null.")
            finish()
            return
        }
        bookFileName = receivedFileName // Assign ke properti non-nullable
        bookTitle = receivedTitle // Assign ke properti non-nullable


        Log.d("PdfViewerDebug", "PdfViewerActivity started. Book ID: $bookId, File Name: $bookFileName, Title: $bookTitle")

        supportActionBar?.title = bookTitle // <--- Sekarang bookTitle sudah String (non-nullable)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val downloadsDir = File(getExternalFilesDir(null), "books_downloaded")
        if (!downloadsDir.exists()) {
            Log.e("PdfViewerDebug", "Download directory does not exist: ${downloadsDir.absolutePath}")
            Toast.makeText(this, "Direktori download tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val pdfFile = File(downloadsDir, bookFileName) // bookFileName sudah String (non-nullable)

        Log.d("PdfViewerDebug", "Checking for PDF file: ${pdfFile.absolutePath}, Exists: ${pdfFile.exists()}, Size: ${pdfFile.length()} bytes")

        if (pdfFile.exists() && pdfFile.length() > 0) {
            currentPage = PrefManager.getLastReadPage(this, bookId)
            displayPdf(pdfFile)
        } else {
            val errorMessage = if (pdfFile.exists() && pdfFile.length() == 0L) {
                "File buku ditemukan tapi kosong: ${pdfFile.absolutePath}"
            } else {
                "File buku tidak ditemukan: ${pdfFile.absolutePath}"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            Log.e("PdfViewerDebug", errorMessage)
            finish()
        }
    }

    private fun displayPdf(pdfFile: File) {
        binding.progressBarPdf.visibility = View.VISIBLE
        try {
            binding.pdfView.fromFile(pdfFile)
                .defaultPage(currentPage)
                .onPageChange(this)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
                .spacing(10)
                .load()
        } catch (e: Exception) {
            Log.e("PdfViewerDebug", "Error loading PDF: ${e.message}", e)
            Toast.makeText(this, "Gagal memuat PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            binding.progressBarPdf.visibility = View.GONE
            finish()
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        currentPage = page
        supportActionBar?.subtitle = "Halaman ${currentPage + 1} dari ${pageCount}"
    }

    override fun loadComplete(nbPages: Int) {
        binding.progressBarPdf.visibility = View.GONE
        Toast.makeText(this, "Halaman ${currentPage + 1} dari ${nbPages}", Toast.LENGTH_SHORT).show()
        supportActionBar?.subtitle = "Halaman ${currentPage + 1} dari ${nbPages}"
    }

    override fun onPause() {
        super.onPause()
        PrefManager.saveLastReadPage(this, bookId, currentPage)
        Log.d("PdfViewerDebug", "Bookmark saved for Book ID: $bookId, Page: $currentPage (onPause)")
    }

    override fun onDestroy() {
        super.onDestroy()
        PrefManager.saveLastReadPage(this, bookId, currentPage)
        Log.d("PdfViewerDebug", "Bookmark saved for Book ID: $bookId, Page: $currentPage (onDestroy)")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
        const val EXTRA_BOOK_FILENAME = "extra_book_filename"
        const val EXTRA_BOOK_TITLE = "extra_book_title"
    }
}