package com.insfinal.bookdforall.network

import com.insfinal.bookdforall.utils.SessionManager // <--- PERBAIKAN: Ubah 'session' menjadi 'utils'
import com.squareup.moshi.Moshi
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import android.util.Log // Import Log jika belum ada untuk debugging
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
object RetrofitInstance {
    const val BASE_URL = "http://10.70.4.146:3000/api/"
    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookieManager))
        .addInterceptor { chain ->
            val token = SessionManager.getAuthToken() // <--- PERBAIKAN: Panggil getAuthToken()
            val requestBuilder = chain.request().newBuilder()

            if (token != null && token.isNotEmpty()) { // <--- PERBAIKAN: Menggunakan .isNotEmpty() atau !.isNullOrEmpty() yang benar
                Log.d("RetrofitInstance", "Adding Authorization header with token: $token") // Debugging
                requestBuilder.addHeader("Authorization", "Bearer $token")
            } else {
                Log.d("RetrofitInstance", "No auth token found or it's empty. Not adding Authorization header.") // Debugging
            }

            chain.proceed(requestBuilder.build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))

            .build()
            .create(ApiService::class.java)
    }
}