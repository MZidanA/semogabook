package com.insfinal.bookdforall.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadedBook(
    val bookId: Int,
    val fileName: String,
    val title: String, // Pastikan ini String, bukan String?
    val author: String, // Pastikan ini String, bukan String?
    var lastReadPage: Int = 0
) : Parcelable