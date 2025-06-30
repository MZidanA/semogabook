package com.insfinal.bookdforall.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.repository.BookRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AdminBookViewModel : ViewModel() {

    private val bookRepository = BookRepository()

    private val _booksList = MutableLiveData<MutableList<Book>>()
    val booksList: LiveData<MutableList<Book>> get() = _booksList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _eventSuccess = MutableSharedFlow<String>()
    val eventSuccess: SharedFlow<String> get() = _eventSuccess

    private val _eventError = MutableSharedFlow<String>()
    val eventError: SharedFlow<String> get() = _eventError

    fun loadBooks() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val response = bookRepository.getAll()
                if (response.isSuccessful) {
                    _booksList.value = response.body()?.toMutableList() ?: mutableListOf()
                    Log.d("AdminBookViewModel", "Loaded ${_booksList.value?.size} books.")
                } else {
                    val error = "Failed to load books: ${response.message()}"
                    _errorMessage.value = error
                    Log.e("AdminBookViewModel", error)
                    _eventError.emit(error)
                }
            } catch (e: Exception) {
                val error = "Error loading books: ${e.message}"
                _errorMessage.value = error
                Log.e("AdminBookViewModel", error, e)
                _eventError.emit(error)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBook(bookId: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val response = bookRepository.delete(bookId)
                if (response.isSuccessful) {
                    val currentList = _booksList.value ?: mutableListOf()
                    currentList.removeIf { it.bookId == bookId }
                    _booksList.value = currentList
                    _eventSuccess.emit("Buku berhasil dihapus")
                    Log.d("AdminBookViewModel", "Book $bookId deleted successfully.")
                } else {
                    val errorMsg = "Gagal menghapus buku: ${response.message()}"
                    _errorMessage.value = errorMsg
                    _eventError.emit(errorMsg)
                    Log.e("AdminBookViewModel", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error deleting book: ${e.message}"
                _errorMessage.value = errorMsg
                _eventError.emit(errorMsg)
                Log.e("AdminBookViewModel", errorMsg, e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
