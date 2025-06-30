package com.insfinal.bookdforall.repository

import android.content.Context // <--- TAMBAHKAN INI
import android.net.Uri // <--- TAMBAHKAN INI
import com.insfinal.bookdforall.model.Book
import com.insfinal.bookdforall.model.CreateBookRequest
import com.insfinal.bookdforall.network.RetrofitInstance
import com.insfinal.bookdforall.utils.BookAssetPaths
import com.insfinal.bookdforall.utils.FileUtils // <--- TAMBAHKAN INI
import okhttp3.MediaType.Companion.toMediaTypeOrNull // <--- TAMBAHKAN INI
import okhttp3.MultipartBody // <--- TAMBAHKAN INI
import okhttp3.RequestBody.Companion.asRequestBody // <--- TAMBAHKAN INI (untuk file.asRequestBody)
import okhttp3.RequestBody.Companion.toRequestBody // <--- TAMBAHKAN INI (untuk string.toRequestBody)
import retrofit2.Response
import java.io.File // Ensure correct import for File

class BookRepository {
    private val api = RetrofitInstance.api

    suspend fun getAll(): Response<List<Book>> = api.getBooks()
    suspend fun getAllBooks(): Response<List<Book>> {
        return RetrofitInstance.api.getBooks()
    }
    suspend fun getBookById(id: Int): Response<Book> = api.getBookById(id)

    suspend fun create(req: CreateBookRequest): Response<Unit> = api.createBook(req)

    suspend fun update(id: Int, req: CreateBookRequest): Response<Unit> = api.updateBook(id, req)

    // PERUBAHAN: Tambahkan return type Response<Unit>
    suspend fun uploadBook(
        context: Context,
        judul: String,
        penulis: String,
        deskripsi: String,
        kategori: String,
        harga: Int,
        format: String,
        jumlahHalaman: Int,
        tanggal: String,
        publisherId: Int,
        contentProviderId: Int,
        coverUri: Uri?,
        pdfUri: Uri?
    ): Response<Unit> {
        val titlePart = judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val authorPart = penulis.toRequestBody("text/plain".toMediaTypeOrNull())
        val descPart = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
        val kategoriPart = kategori.toRequestBody("text/plain".toMediaTypeOrNull())
        val hargaPart = harga.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val jumlahHalamanPart = jumlahHalaman.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val formatPart = format.toRequestBody("text/plain".toMediaTypeOrNull())

        val tanggalPart = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
        val publisherIdPart = publisherId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val contentProviderIdPart = contentProviderId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val coverPart = coverUri?.let { uri ->
            val file = FileUtils.fromUri(context, uri)
            MultipartBody.Part.createFormData("cover", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
        }

        val pdfPart = pdfUri?.let { uri ->
            val file = FileUtils.fromUri(context, uri)
            MultipartBody.Part.createFormData("pdf", file.name, file.asRequestBody("application/pdf".toMediaTypeOrNull()))
        }

        return RetrofitInstance.api.uploadBook(
            titlePart,
            authorPart,
            descPart,
            kategoriPart,
            hargaPart,
            formatPart,
            jumlahHalamanPart,
            tanggalPart,
            publisherIdPart,
            contentProviderIdPart,
            coverPart,
            pdfPart
        )
    }
    suspend fun UpdateuploadBook(
        context: Context,
        judul: String,
        penulis: String,
        deskripsi: String,
        kategori: String,
        harga: Int,
        format: String,
        jumlahHalaman: Int,
        tanggal: String,
        publisherId: Int,
        contentProviderId: Int,
        coverUri: Uri?,
        pdfUri: Uri?
    ): Response<Unit> {
        val titlePart = judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val authorPart = penulis.toRequestBody("text/plain".toMediaTypeOrNull())
        val descPart = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
        val kategoriPart = kategori.toRequestBody("text/plain".toMediaTypeOrNull())
        val hargaPart = harga.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val jumlahHalamanPart = jumlahHalaman.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val formatPart = format.toRequestBody("text/plain".toMediaTypeOrNull())

        val tanggalPart = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
        val publisherIdPart = publisherId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val contentProviderIdPart = contentProviderId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val coverPart = coverUri?.let { uri ->
            val file = FileUtils.fromUri(context, uri)
            MultipartBody.Part.createFormData("cover", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
        }

        val pdfPart = pdfUri?.let { uri ->
            val file = FileUtils.fromUri(context, uri)
            MultipartBody.Part.createFormData("pdf", file.name, file.asRequestBody("application/pdf".toMediaTypeOrNull()))
        }

        return RetrofitInstance.api.uploadBook(
            titlePart,
            authorPart,
            descPart,
            kategoriPart,
            hargaPart,
            formatPart,
            jumlahHalamanPart,
            tanggalPart,
            publisherIdPart,
            contentProviderIdPart,
            coverPart,
            pdfPart
        )
    }


    suspend fun delete(id: Int): Response<Unit> = api.deleteBook(id)

    suspend fun getContinueReadingBooks(): Response<List<Book>> = api.getContinueReading()

    suspend fun getTrendingBooks(): Response<List<Book>> = api.getTrendingBooks()

}