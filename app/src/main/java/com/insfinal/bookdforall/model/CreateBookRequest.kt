package com.insfinal.bookdforall.model

import com.squareup.moshi.Json

data class CreateBookRequest(
    val judul: String,
    val penulis: String,
    val deskripsi: String,
    val kategori: String,
    val harga: Double,
    val format: String,

    @Json(name = "cover_image_url")
    val coverImageUrl: String? = null, // bisa null jika pakai multipart upload

    @Json(name = "pdf_url")
    val pdfUrl: String? = null, // bisa null jika pakai multipart upload

    @Json(name = "jumlah_halaman")
    val jumlahHalaman: Int,

    val tanggal: String,

    @Json(name = "publisher_id")
    val publisherId: Int? = null, // boleh null, tergantung logika backend

    @Json(name = "content_provider_id")
    val contentProviderId: Int? = null // boleh null, tergantung logika backend
)
