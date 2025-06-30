package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

data class CreateUserRequest(
    val nama: String,
    val email: String,
    val password: String,
    @Json(name = "tanggal_daftar") val tanggalDaftar: String,
    @Json(name = "status_langganan") val statusLangganan: String
)
