package com.insfinal.bookdforall.model

data class SetNewPasswordRequest(
    val email: String,
    val newPassword: String
)
