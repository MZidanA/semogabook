// HomeViewModel.kt
package com.insfinal.bookdforall.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.repository.BookRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _allBooks = MutableLiveData<List<Book>>()
    private val _categorizedBooks = MutableLiveData<Map<String, List<Book>>>()
    private val _trendingBooks = MutableLiveData<List<Book>>()
    val trendingBooks: LiveData<List<Book>> = _trendingBooks
    val categorizedBooks: LiveData<Map<String, List<Book>>> = _categorizedBooks


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchBooks()
    }
    private fun normalizeCategoryName(name: String): String {
        return name.trim().lowercase().replaceFirstChar { it.uppercase() }
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = BookRepository().getAllBooks()
                if (response.isSuccessful) {
                    val books = response.body() ?: emptyList()
                    _allBooks.value = books
                    Log.d("API", "Book data: $books")

                    // Trending acak 5 buku
                    _trendingBooks.value = books.shuffled().take(5)

                    // Kelompokkan buku berdasarkan kategori dinamis
                    val groupedByCategory = books.groupBy { it.kategori.lowercase() }
                    _categorizedBooks.value = groupedByCategory

                } else {
                    _errorMessage.value = "Gagal memuat buku: ${response.message()}"
                    Log.e("API", "Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun filterBooks(query: String) {
        val books = _allBooks.value ?: return
        val filteredGrouped = books
            .filter { it.judul.contains(query, ignoreCase = true) }
            .groupBy { normalizeCategoryName(it.kategori) }

        _categorizedBooks.value = filteredGrouped
    }

}
