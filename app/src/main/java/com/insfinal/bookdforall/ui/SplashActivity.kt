package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivitySplashBinding // Corrected package for generated binding class

// Kelas Activity untuk menampilkan layar pembuka (splash screen)
class SplashActivity : AppCompatActivity() {

    // Durasi waktu tampilan splash screen (3000 milidetik = 3 detik)
    private val SPLASH_TIME_OUT: Long = 3000
    // Deklarasi variabel binding untuk mengakses elemen UI
    private lateinit var binding: ActivitySplashBinding

    // Metode yang dipanggil saat Activity pertama kali dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Panggil onCreate dari kelas induk
        binding = ActivitySplashBinding.inflate(layoutInflater) // Inisialisasi binding
        setContentView(binding.root) // Set layout Activity

        // Menggunakan Handler untuk menunda eksekusi kode
        Handler(Looper.getMainLooper()).postDelayed({ // Dapatkan Handler yang berjalan di Main Looper (UI Thread), lalu tunda eksekusi
            val intent = Intent(this, LoginActivity::class.java) // Buat Intent untuk memulai LoginActivity
            startActivity(intent) // Mulai LoginActivity

            finish() // Tutup SplashActivity agar tidak bisa kembali ke sini
        }, SPLASH_TIME_OUT) // Tunda eksekusi selama SPLASH_TIME_OUT
    }
}