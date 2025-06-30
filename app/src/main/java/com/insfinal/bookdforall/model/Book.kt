package com.insfinal.bookdforall.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    @Json(name = "bookId")
    val bookId: Int,

    @Json(name = "judul")
    val judul: String,

    @Json(name = "penulis")
    val penulis: String,

    @Json(name = "deskripsi")
    val deskripsi: String,

    @Json(name = "kategori")
    val kategori: String,

    @Json(name = "harga")
    val harga: Double,

    @Json(name = "format")
    val format: String,

    @Json(name = "cover_image_url")
    val coverImageUrl: String?,

    @Json(name = "jumlah_halaman")
    val jumlahHalaman: Int,

    @Json(name = "tanggal")
    val tanggal: String?,

    @Json(name = "pdf_url")
    val pdfUrl: String?,

    @Json(name = "publisher_id")
    val publisherId: Int,

    @Json(name = "content_provider_id")
    val contentProviderId: Int,

    @Json(name = "rating")
    val rating: Float? = null,

    @Json(name = "ratingCount")
    val ratingCount: Int? = null
) : Parcelable
