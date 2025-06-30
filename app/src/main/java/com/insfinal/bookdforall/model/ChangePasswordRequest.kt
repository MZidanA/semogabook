package com.insfinal.bookdforall.model

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)