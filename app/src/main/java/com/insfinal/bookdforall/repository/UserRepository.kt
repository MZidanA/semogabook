package com.insfinal.bookdforall.repository

import com.insfinal.bookdforall.model.*
import com.insfinal.bookdforall.network.RetrofitInstance
import retrofit2.Response

class UserRepository {
    private val api = RetrofitInstance.api

    // ✅ Ambil semua user
    suspend fun getAll(): Response<List<User>> = api.getUsers()

    // ✅ Ambil satu user berdasarkan ID
    suspend fun getOne(id: Int): Response<User> = api.getUser(id)

    // ✅ Buat user baru
    suspend fun create(req: CreateUserRequest): Response<Unit> = api.createUser(req)

    // ✅ Perbarui data user berdasarkan ID
    suspend fun updateUser(id: Int, req: CreateUserRequest): Response<Unit> = api.updateUser(id, req)

    // ✅ Hapus user berdasarkan ID
    suspend fun delete(id: Int): Response<Unit> = api.deleteUser(id)

    // ✅ Ambil data user yang sedang login dari token
    suspend fun getCurrentUser(): Response<User> = api.getCurrentUser()

    // ✅ Ubah password user yang sedang login
    suspend fun changePassword(oldPass: String, newPass: String): Response<Unit> {
        return api.changePassword(ChangePasswordRequest(oldPass, newPass))
    }

    // ✅ Login
    suspend fun login(req: LoginRequest): Response<LoginResponse> = api.login(req)

    // ✅ Lupa password
    suspend fun forgotPassword(email: String): Response<Unit> {
        return api.forgotPassword(ForgotPasswordRequest(email))
    }

    // ✅ Hanya alias dari getAll(), dipanggil di UserManageViewModel
    suspend fun getAllUsers(): Response<List<User>> = api.getUsers()

    // ✅ Hapus user berdasarkan ID (jika dipanggil dari ViewModel/Admin)
    suspend fun deleteUser(userId: Int): Response<Unit> = api.deleteUser(userId)
}
