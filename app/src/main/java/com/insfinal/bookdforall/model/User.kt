package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

data class User(
    @Json(name = "user_id")
    val userId: Int,

    val nama: String,
    val email: String,
    val password: String,

    @Json(name = "is_admin")
    val isAdmin: Boolean = false,

    @Json(name = "tanggal_daftar")
    val tanggalDaftar: String?,

    @Json(name = "status_langganan")
    val statusLangganan: String?
)
