package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.insfinal.bookdforall.databinding.ActivitySetNewPasswordBinding
import com.insfinal.bookdforall.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SetNewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")

        binding.btnSetPassword.setOnClickListener {
            val newPassword = binding.etNewPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (email.isNullOrBlank()) {
                showToast("Email tidak tersedia.")
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                showToast("Password minimal 6 karakter.")
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                showToast("Konfirmasi password tidak cocok.")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = AuthRepository().setNewPassword(email, newPassword)
                    if (response.isSuccessful) {
                        showToast("Password berhasil diperbarui.")
                        navigateToLogin()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Gagal memperbarui password"
                        showToast(errorMsg)
                    }
                } catch (e: IOException) {
                    showToast("Koneksi gagal: ${e.localizedMessage}")
                } catch (e: HttpException) {
                    showToast("Server error: ${e.code()} - ${e.message}")
                } catch (e: Exception) {
                    showToast("Terjadi kesalahan: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this@SetNewPasswordActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
