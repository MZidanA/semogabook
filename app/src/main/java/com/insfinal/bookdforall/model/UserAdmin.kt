package com.insfinal.bookdforall.model

data class UserAdmin(
    val userId: Int,
    val nama: String,
    val email: String,
    val password: String,
    val isAdmin: Boolean,
    val tanggalDaftar: String?,
    val statusLangganan: String?
)