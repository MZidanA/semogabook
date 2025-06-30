package com.insfinal.bookdforall.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.insfinal.bookdforall.model.Publisher
import com.insfinal.bookdforall.repository.PublisherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PublisherViewModel : ViewModel() {

    private val repository = PublisherRepository()

    private val _publishers = MutableLiveData<List<Publisher>>()
    val publishers: LiveData<List<Publisher>> = _publishers

    private val _eventSuccess = MutableSharedFlow<String>()
    val eventSuccess = _eventSuccess.asSharedFlow()

    private val _eventError = MutableSharedFlow<String>()
    val eventError = _eventError.asSharedFlow()

    fun loadPublishers() {
        viewModelScope.launch {
            try {
                val response = repository.getAllPublishers()
                if (response.isSuccessful) {
                    _publishers.value = response.body() ?: emptyList()
                } else {
                    _eventError.emit("Gagal memuat penerbit")
                }
            } catch (e: Exception) {
                _eventError.emit("Error: ${e.localizedMessage}")
            }
        }
    }

    fun createPublisher(publisher: Publisher) {
        viewModelScope.launch {
            try {
                val response = repository.createPublisher(publisher)
                if (response.isSuccessful) {
                    _eventSuccess.emit("Penerbit ditambahkan")
                    loadPublishers()
                } else {
                    _eventError.emit("Gagal menambah penerbit")
                }
            } catch (e: Exception) {
                _eventError.emit("Error: ${e.localizedMessage}")
            }
        }
    }

    fun updatePublisher(id: Int, publisher: Publisher) {
        viewModelScope.launch {
            try {
                val response = repository.updatePublisher(id, publisher)
                if (response.isSuccessful) {
                    _eventSuccess.emit("Penerbit diperbarui")
                    loadPublishers()
                } else {
                    _eventError.emit("Gagal memperbarui penerbit")
                }
            } catch (e: Exception) {
                _eventError.emit("Error: ${e.localizedMessage}")
            }
        }
    }

    fun deletePublisher(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deletePublisher(id)
                if (response.isSuccessful) {
                    _eventSuccess.emit("Penerbit dihapus")
                    loadPublishers()
                } else {
                    _eventError.emit("Gagal menghapus penerbit")
                }
            } catch (e: Exception) {
                _eventError.emit("Error: ${e.localizedMessage}")
            }
        }
    }
}
