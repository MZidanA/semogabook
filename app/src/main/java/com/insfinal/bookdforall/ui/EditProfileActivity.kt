package com.insfinal.bookdforall.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.model.CreateUserRequest
import com.insfinal.bookdforall.model.User
import com.insfinal.bookdforall.repository.UserRepository
import com.insfinal.bookdforall.utils.SessionManager
import kotlinx.coroutines.launch

// Kelas EditProfileActivity bertanggung jawab untuk mengedit profil pengguna.
class EditProfileActivity : AppCompatActivity() {

    // Deklarasi variabel untuk elemen UI.
    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSimpan: Button

    // 🔁 Fungsi ekstensi untuk mengonversi objek User ke CreateUserRequest.
    private fun User.toCreateUserRequest(nama: String, email: String): CreateUserRequest {
        return CreateUserRequest(
            nama = nama, // Menggunakan nama baru.
            email = email, // Menggunakan email baru.
            password = this.password, // Mempertahankan password yang sudah ada.
            tanggalDaftar = this.tanggalDaftar ?: "", // Mempertahankan tanggal daftar atau string kosong.
            statusLangganan = this.statusLangganan ?: "" // Mempertahankan status langganan atau string kosong.
        )
    }

    // Dipanggil saat aktivitas pertama kali dibuat.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mengatur tampilan konten aktivitas dari layout activity_edit_profile.xml.
        setContentView(R.layout.activity_edit_profile)

        // Menginisialisasi elemen UI dengan ID yang sesuai dari layout.
        etNama = findViewById(R.id.et_nama)
        etEmail = findViewById(R.id.et_email)
        btnSimpan = findViewById(R.id.btn_simpan)
        val btnKembali: ImageButton = findViewById(R.id.btn_kembali)

        // Mendapatkan ID pengguna dari SessionManager.
        val userId = SessionManager.getUserId()
        // Memeriksa apakah ID pengguna valid.
        if (userId == -1) {
            Toast.makeText(this, "Session tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            finish() // Menutup aktivitas jika session tidak ditemukan.
            return
        }

        // ✅ Mengambil data user berdasarkan ID.
        lifecycleScope.launch {
            try {
                // Memanggil fungsi getOne dari UserRepository untuk mendapatkan data pengguna.
                val response = UserRepository().getOne(userId)
                // Memeriksa apakah permintaan berhasil.
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        etNama.setText(user.nama) // Mengisi EditText nama dengan data pengguna.
                        etEmail.setText(user.email) // Mengisi EditText email dengan data pengguna.
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Gagal memuat data user", Toast.LENGTH_SHORT).show()
                    Log.e("EditProfile", "Fetch user gagal: ${response.code()}") // Mencatat error ke Logcat.
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Gagal ambil data user", e) // Mencatat exception ke Logcat.
                Toast.makeText(this@EditProfileActivity, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ Mengatur OnClickListener untuk tombol "Simpan".
        btnSimpan.setOnClickListener {
            val namaBaru = etNama.text.toString().trim() // Mengambil nama baru dari EditText.
            val emailBaru = etEmail.text.toString().trim() // Mengambil email baru dari EditText.

            // Validasi apakah nama dan email tidak kosong.
            if (namaBaru.isEmpty() || emailBaru.isEmpty()) {
                Toast.makeText(this, "Nama dan email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Menghentikan eksekusi jika ada kolom kosong.
            }

            lifecycleScope.launch {
                try {
                    // Mendapatkan data pengguna saat ini lagi untuk memastikan integritas data (misalnya password).
                    val user = UserRepository().getOne(userId).body()
                    if (user != null) {
                        // Mengonversi objek User menjadi CreateUserRequest dengan nama dan email yang baru.
                        val request = user.toCreateUserRequest(namaBaru, emailBaru)
                        // Memanggil fungsi updateUser dari UserRepository untuk memperbarui data pengguna.
                        val updateResponse = UserRepository().updateUser(userId, request)

                        // Memeriksa apakah pembaruan berhasil.
                        if (updateResponse.isSuccessful) {
                            Toast.makeText(this@EditProfileActivity, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                            finish() // Menutup aktivitas setelah berhasil disimpan.
                        } else {
                            Toast.makeText(this@EditProfileActivity, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
                            Log.e("EditProfile", "Update gagal: ${updateResponse.code()}") // Mencatat error ke Logcat.
                        }
                    }
                } catch (e: Exception) {
                    Log.e("EditProfile", "Gagal update user", e) // Mencatat exception ke Logcat.
                    Toast.makeText(this@EditProfileActivity, "Kesalahan koneksi saat update", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Mengatur OnClickListener untuk tombol "Kembali".
        btnKembali.setOnClickListener {
            finish() // Menutup aktivitas saat tombol kembali ditekan.
        }
    }
}