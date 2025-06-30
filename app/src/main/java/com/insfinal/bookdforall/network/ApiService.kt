package com.insfinal.bookdforall.network

import com.insfinal.bookdforall.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequest): Response<Unit>

    @POST("auth/reset-password")
    suspend fun setNewPassword(@Body req: SetNewPasswordRequest): Response<Unit>

    // --- USER ---
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>

    @GET("users/me")
    suspend fun getCurrentUser(): Response<User>

    @POST("users")
    suspend fun createUser(@Body body: CreateUserRequest): Response<Unit>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body body: CreateUserRequest): Response<Unit>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @PUT("users/change-password")
    suspend fun changePassword(@Body req: ChangePasswordRequest): Response<Unit>

    // --- ADMIN ---
    @GET("admins")
    suspend fun getAdmins(): Response<List<Admin>>

    @GET("admins/{id}")
    suspend fun getAdmin(@Path("id") id: Int): Response<Admin>

    @POST("admins")
    suspend fun createAdmin(@Body req: CreateAdminRequest): Response<Unit>

    @PUT("admins/{id}")
    suspend fun updateAdmin(@Path("id") id: Int, @Body req: CreateAdminRequest): Response<Unit>

    @DELETE("admins/{id}")
    suspend fun deleteAdmin(@Path("id") id: Int): Response<Unit>

    // --- BOOK ---
    @GET("books")
    suspend fun getBooks(): Response<List<Book>>

    @GET("books/{id}")
    suspend fun getBookById(@Path("id") id: Int): Response<Book>

    @POST("books")
    suspend fun createBook(@Body req: CreateBookRequest): Response<Unit>

    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: Int, @Body req: CreateBookRequest): Response<Unit>

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: Int): Response<Unit>

    @GET("books/continue")
    suspend fun getContinueReading(): Response<List<Book>>

    @GET("books/trending")
    suspend fun getTrendingBooks(): Response<List<Book>>

    @Multipart
    @POST("books/upload")
    suspend fun uploadBook(
        @Part("judul") judul: RequestBody,
        @Part("penulis") penulis: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("format") format: RequestBody,
        @Part("jumlah_halaman") jumlahHalaman: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("publisher_id") publisherId: RequestBody,
        @Part("content_provider_id") contentProviderId: RequestBody,
        @Part cover: MultipartBody.Part?,
        @Part pdf: MultipartBody.Part?
    ): Response<Unit>

    @Multipart
    @PUT("books/upload/{id}")
    suspend fun updateBookWithFiles(
        @Path("id") id: Int,
        @Part("judul") judul: RequestBody,
        @Part("penulis") penulis: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("format") format: RequestBody,
        @Part("jumlah_halaman") jumlahHalaman: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("publisher_id") publisherId: RequestBody,
        @Part("content_provider_id") contentProviderId: RequestBody,
        @Part cover: MultipartBody.Part?,
        @Part pdf: MultipartBody.Part?
    ): Response<Unit>

    // --- PUBLISHER ---
    @GET("publishers")
    suspend fun getAllPublishers(): Response<List<Publisher>>

    @POST("publishers")
    suspend fun createPublisher(@Body publisher: Publisher): Response<Unit>

    @PUT("publishers/{id}")
    suspend fun updatePublisher(@Path("id") id: Int, @Body publisher: Publisher): Response<Unit>

    @DELETE("publishers/{id}")
    suspend fun deletePublisher(@Path("id") id: Int): Response<Unit>
}
