//package com.insfinal.bookdforall.model
//
//data class LoginResponse(
//    val message: String,
//    val user: UserSummary
//)
//
//data class UserSummary(
//    val id: Int,
//    val nama: String,
//    val email: String,
//    val isAdmin: Boolean
//)

package com.insfinal.bookdforall.model

import com.squareup.moshi.Json // Tambahkan jika menggunakan Moshi

data class LoginResponse(
    val message: String,
    val user: UserSummary,
    @Json(name = "access_token") val accessToken: String? // <--- Tambahkan ini
    // Atau val token: String? jika API Anda menggunakan nama 'token'
)

data class UserSummary(
    val id: Int,
    val nama: String,
    val email: String,
    val isAdmin: Boolean
)