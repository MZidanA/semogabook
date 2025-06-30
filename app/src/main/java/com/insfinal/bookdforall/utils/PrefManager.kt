package com.insfinal.bookdforall.utils

import android.content.Context
import android.content.SharedPreferences
import com.insfinal.bookdforall.model.DownloadedBook
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PrefManager {
    private const val PREF_NAME = "BookAppPrefs"
    private const val KEY_LAST_READ_PAGE_PREFIX = "last_read_page_"
    private const val KEY_DOWNLOADED_BOOKS = "downloaded_books"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLastReadPage(context: Context, bookId: Int, page: Int) {
        getPrefs(context).edit().putInt(KEY_LAST_READ_PAGE_PREFIX + bookId, page).apply()
    }

    fun getLastReadPage(context: Context, bookId: Int): Int {
        return getPrefs(context).getInt(KEY_LAST_READ_PAGE_PREFIX + bookId, 0)
    }

    // Perbarui fungsi ini untuk menerima judul dan penulis
    fun addDownloadedBook(context: Context, bookId: Int, fileName: String, title: String, author: String) {
        val editor = getPrefs(context).edit()
        val gson = Gson()
        val existingBooksJson = getPrefs(context).getString(KEY_DOWNLOADED_BOOKS, null)
        val type = object : TypeToken<MutableList<DownloadedBook>>() {}.type
        val downloadedBooks: MutableList<DownloadedBook> =
            if (existingBooksJson != null) gson.fromJson(existingBooksJson, type) else mutableListOf()

        // Periksa apakah buku sudah ada, jika ya, perbarui, jika tidak, tambahkan
        val existingBookIndex = downloadedBooks.indexOfFirst { it.bookId == bookId }
        if (existingBookIndex != -1) {
            // Buku sudah ada, mungkin hanya perlu update lastReadPage (jika perlu di sini)
            // downloadedBooks[existingBookIndex].lastReadPage = 0 // Reset jika baru diunduh, atau biarkan
        } else {
            downloadedBooks.add(DownloadedBook(bookId, fileName, title, author))
        }
        val updatedJson = gson.toJson(downloadedBooks)
        editor.putString(KEY_DOWNLOADED_BOOKS, updatedJson).apply()
    }

    fun getDownloadedBooks(context: Context): List<DownloadedBook> {
        val gson = Gson()
        val json = getPrefs(context).getString(KEY_DOWNLOADED_BOOKS, null)
        val type = object : TypeToken<List<DownloadedBook>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun removeDownloadedBook(context: Context, bookId: Int) {
        val editor = getPrefs(context).edit()
        val gson = Gson()
        val existingBooksJson = getPrefs(context).getString(KEY_DOWNLOADED_BOOKS, null)
        val type = object : TypeToken<MutableList<DownloadedBook>>() {}.type
        val downloadedBooks: MutableList<DownloadedBook> =
            if (existingBooksJson != null) gson.fromJson(existingBooksJson, type) else mutableListOf()

        downloadedBooks.removeIf { it.bookId == bookId }
        val updatedJson = gson.toJson(downloadedBooks)
        editor.putString(KEY_DOWNLOADED_BOOKS, updatedJson).apply()
        editor.remove(KEY_LAST_READ_PAGE_PREFIX + bookId).apply()
    }
}