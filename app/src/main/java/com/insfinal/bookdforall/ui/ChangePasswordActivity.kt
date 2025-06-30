package com.insfinal.bookdforall.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.insfinal.bookdforall.databinding.ActivityChangePasswordBinding
import com.insfinal.bookdforall.model.ErrorResponse
import com.insfinal.bookdforall.repository.UserRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch

// Kelas ChangePasswordActivity bertanggung jawab untuk mengubah password pengguna.
class ChangePasswordActivity : AppCompatActivity() {

    // Binding untuk mengakses elemen-elemen UI dari layout activity_change_password.xml.
    private lateinit var binding: ActivityChangePasswordBinding

    // Dipanggil saat aktivitas pertama kali dibuat.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginisialisasi binding dengan mengembang (inflate) layout.
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        // Mengatur tampilan konten aktivitas.
        setContentView(binding.root)

        // Mengatur OnClickListener untuk tombol "Ganti Password".
        binding.btnChangePassword.setOnClickListener {
            // Mengambil teks dari EditText untuk password lama, baru, dan konfirmasi.
            val oldPass = binding.etOldPassword.text.toString().trim()
            val newPass = binding.etNewPassword.text.toString().trim()
            val confirmPass = binding.etConfirmNewPassword.text.toString().trim()

            // Memvalidasi apakah semua kolom sudah diisi.
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Menghentikan eksekusi jika ada kolom kosong.
            }
            // Memvalidasi apakah password baru dan konfirmasi password cocok.
            if (newPass != confirmPass) {
                Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Menghentikan eksekusi jika password tidak cocok.
            }
            // Menonaktifkan tombol dan mengubah teksnya saat proses sedang berjalan.
            binding.btnChangePassword.isEnabled = false
            binding.btnChangePassword.text = "Memproses..."
            // Memanggil fungsi untuk mengubah password.
            changePassword(oldPass, newPass)
        }
        // Mengatur OnClickListener untuk tombol "Kembali".
        binding.btnKembali.setOnClickListener {
            finish() // Menutup aktivitas saat tombol kembali ditekan.
        }
    }

    // Fungsi untuk memanggil API penggantian password.
    private fun changePassword(oldPass: String, newPass: String) {
        // Meluncurkan coroutine dalam lifecycleScope untuk operasi asinkron.
        lifecycleScope.launch {
            try {
                // Memanggil fungsi changePassword dari UserRepository.
                val response = UserRepository().changePassword(oldPass, newPass)
                // Memeriksa apakah permintaan berhasil (kode status 2xx).
                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                    finish() // Menutup aktivitas setelah password berhasil diubah.
                } else {
                    // Mendapatkan body error dari respons yang gagal.
                    val rawErrorBody = response.errorBody()?.string()
                    // Menginisialisasi Moshi untuk parsing JSON.
                    val moshi = Moshi.Builder().build()
                    // Membuat adapter untuk mengonversi JSON ke objek ErrorResponse.
                    val adapter = moshi.adapter(ErrorResponse::class.java)
                    // Mengurai body error menjadi objek ErrorResponse.
                    val error = adapter.fromJson(rawErrorBody ?: "")
                    // Mendapatkan pesan error dari objek ErrorResponse atau rawErrorBody jika parsing gagal.
                    val errorMessage = error?.message ?: rawErrorBody ?: "Terjadi kesalahan"
                    Toast.makeText(this@ChangePasswordActivity, "Gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Menangkap dan mencatat setiap exception yang terjadi.
                Log.e("ChangePassword", "Error", e)
            } finally {
                // Mengaktifkan kembali tombol dan mengembalikan teksnya setelah proses selesai (baik berhasil atau gagal).
                binding.btnChangePassword.isEnabled = true
                binding.btnChangePassword.text = "Ganti Password"
            }
        }
    }
}