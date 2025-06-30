// UserManageViewModel.kt
package com.insfinal.bookdforall.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.insfinal.bookdforall.model.User
import com.insfinal.bookdforall.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import android.util.Log
import java.util.Locale

class UserManageViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _eventSuccess = MutableSharedFlow<String>()
    val eventSuccess = _eventSuccess.asSharedFlow()

    private val _eventError = MutableSharedFlow<String>()
    val eventError = _eventError.asSharedFlow()

    fun loadUsers() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAllUsers()
                if (response.isSuccessful) {
                    val allUsers = response.body()
                    // Filter out admin users and userId 2 before setting the value
                    _users.value = if (!allUsers.isNullOrEmpty()) {
                        allUsers.filter { !it.isAdmin && it.userId != 2 } // Menambahkan filter untuk userId 2 dan pengguna non-admin
                    } else {
                        emptyList()
                    }
                } else {
                    val errorMsg = "Gagal memuat data user: ${response.code()} - ${response.message()}"
                    _eventError.emit(errorMsg)
                    Log.e("UserManageViewModel", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.localizedMessage}"
                _eventError.emit(errorMsg)
                Log.e("UserManageViewModel", "Error loading users: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: Int) { //
        _isLoading.value = true //
        viewModelScope.launch { //
            try { //
                val response = repository.deleteUser(userId) //
                if (response.isSuccessful) { //
                    loadUsers() // Reload users after successful deletion
                    _eventSuccess.emit("User berhasil dihapus")
                } else { //
                    val errorMsg = "Gagal menghapus user: ${response.code()} - ${response.message()}"
                    _eventError.emit(errorMsg)
                    Log.e("UserManageViewModel", errorMsg)
                }
            } catch (e: Exception) { //
                val errorMsg = "Error: ${e.localizedMessage}"
                _eventError.emit(errorMsg)
                Log.e("UserManageViewModel", "Error deleting user: ${e.localizedMessage}", e)
            } finally { //
                _isLoading.value = false
            }
        }
    }

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    val filteredUsers: LiveData<List<User>> = MediatorLiveData<List<User>>().apply {
        addSource(users) { value = filter(users.value, searchQuery.value) }
        addSource(_searchQuery) { value = filter(users.value, searchQuery.value) }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun filter(users: List<User>?, query: String?): List<User> {
        if (users == null || query.isNullOrBlank()) return users ?: emptyList()
        val q = query.lowercase(Locale.getDefault())
        return users.filter {
            it.nama.lowercase(Locale.getDefault()).contains(q) || it.email.lowercase(Locale.getDefault()).contains(q)
        }
    }
}