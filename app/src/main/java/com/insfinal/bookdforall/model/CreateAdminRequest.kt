package com.insfinal.bookdforall.model
import com.squareup.moshi.Json
data class CreateAdminRequest(
    @Json(name="user_id") val userId: Int,
    val role: String
)
