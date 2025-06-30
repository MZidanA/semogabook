package com.insfinal.bookdforall.model

data class ErrorResponse(
    val status: String,
    val message: String?,
    val code: Int? = null
)
