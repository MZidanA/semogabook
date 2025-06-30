package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.insfinal.bookdforall.databinding.ActivityForgotPasswordBinding
import com.insfinal.bookdforall.repository.AuthRepository
import kotlinx.coroutines.launch

// Kelas ForgotPasswordActivity bertanggung jawab untuk alur lupa password.
class ForgotPasswordActivity : AppCompatActivity() {

    // Binding untuk mengakses elemen-elemen UI dari layout activity_forgot_password.xml.
    private lateinit var binding: ActivityForgotPasswordBinding

    // Dipanggil saat aktivitas pertama kali dibuat.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginisialisasi binding dengan mengembang (inflate) layout.
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        // Mengatur tampilan konten aktivitas.
        setContentView(binding.root)

        // Mengatur OnClickListener untuk tombol "Kirim Link Reset".
        binding.btnSendResetLink.setOnClickListener {
            // Mengambil teks email yang dimasukkan pengguna.
            val enteredEmail = binding.editTextEmail.text.toString().trim()

            // 1. Validasi format email.
            if (!Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
                Toast.makeText(this, "Format email tidak valid.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Menghentikan eksekusi jika format email tidak valid.
            }

            // 2. Panggil API forgotPassword.
            // Meluncurkan coroutine dalam lifecycleScope untuk operasi asinkron.
            lifecycleScope.launch {
                try {
                    // Memanggil fungsi forgotPassword dari AuthRepository.
                    val response = AuthRepository().forgotPassword(enteredEmail)

                    // Memeriksa apakah permintaan berhasil.
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Silakan atur password baru Anda.",
                            Toast.LENGTH_LONG
                        ).show()

                        // Membuat Intent untuk memulai SetNewPasswordActivity.
                        val intent = Intent(this@ForgotPasswordActivity, SetNewPasswordActivity::class.java)
                        intent.putExtra("email", enteredEmail) // Meneruskan email ke aktivitas berikutnya.
                        startActivity(intent) // Memulai aktivitas.
                        finish() // Menutup ForgotPasswordActivity.
                    } else {
                        // Menampilkan pesan error jika email tidak ditemukan atau tidak terdaftar.
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Email tidak ditemukan atau tidak terdaftar.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    // Menampilkan pesan error jika terjadi kesalahan koneksi.
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Kesalahan koneksi: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Mengatur OnClickListener untuk tombol "Kembali".
        binding.btnKembali.setOnClickListener {
            finish() // Menutup aktivitas saat tombol kembali ditekan.
        }
    }
}