package com.insfinal.bookdforall.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalBook(
    val originalBook: Book, // Referensi ke objek Book asli
    val pdfFileName: String, // Nama file PDF di folder assets
    val coverImageFileName: String // Nama file gambar cover di folder assets/assetsimg
) : Parcelable