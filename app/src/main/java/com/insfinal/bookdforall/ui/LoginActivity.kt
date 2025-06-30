package com.insfinal.bookdforall.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.insfinal.bookdforall.databinding.ActivityLoginBinding
import com.insfinal.bookdforall.utils.SessionManager
import com.insfinal.bookdforall.network.RetrofitInstance
import com.insfinal.bookdforall.model.LoginRequest
import com.insfinal.bookdforall.model.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this) // ⬅️ Penting, jangan lupa inisialisasi
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Cek apakah user sudah login
        if (SessionManager.isLoggedIn()) {
            if (SessionManager.isAdmin()) {
                navigateToAdminHome()
            } else {
                navigateToUserHome()
            }
            finish()
            return
        }

        // ✅ Tombol Login ditekan
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmailLogin.text.toString().trim()
            val password = binding.editTextPasswordLogin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = LoginRequest(email, password)
                    val response = RetrofitInstance.api.login(request)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                val authToken = loginResponse.accessToken

                                if (authToken != null) {
                                    // ✅ Simpan token, role admin, dan userId
                                    SessionManager.createLoginSession(authToken, loginResponse.user.isAdmin)
                                    SessionManager.saveUserId(loginResponse.user.id)

                                    Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()

                                    if (loginResponse.user.isAdmin) {
                                        navigateToAdminHome()
                                    } else {
                                        navigateToUserHome()
                                    }
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Login berhasil, namun token tidak ditemukan.", Toast.LENGTH_SHORT).show()
                                    Log.e("LoginActivity", "Login berhasil tapi token null.")
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "Login berhasil, namun respons user kosong.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@LoginActivity, "Login gagal: ${errorBody ?: response.message()}", Toast.LENGTH_LONG).show()
                            Log.e("LoginActivity", "Login gagal: ${response.code()} - ${errorBody ?: response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Error koneksi: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("LoginActivity", "API Login error: ${e.message}", e)
                    }
                }
            }
        }

        binding.textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToUserHome() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun navigateToAdminHome() {
        startActivity(Intent(this, AdminActivity::class.java))
    }
}
