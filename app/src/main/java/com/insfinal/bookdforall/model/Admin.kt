package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

data class Admin(
    @Json(name="admin_id") val adminId: Int,
    @Json(name="user_id") val userId: Int,
    val role: String
)