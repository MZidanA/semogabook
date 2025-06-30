package com.insfinal.bookdforall.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SessionManager {

    private const val PREF_NAME = "UserSession"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_AUTH_TOKEN = "authToken"
    private const val KEY_IS_ADMIN = "isAdmin"
    private const val KEY_USER_ID = "userId" // ✅ Untuk menyimpan ID user

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    // ✅ Panggil ini di Application atau di Activity onCreate
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    // ✅ Simpan token & role admin
    fun createLoginSession(authToken: String, isAdmin: Boolean) {
        if (!this::editor.isInitialized) {
            Log.e("SessionManager", "Editor not initialized. Call init(context) first.")
            return
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_AUTH_TOKEN, authToken)
        editor.putBoolean(KEY_IS_ADMIN, isAdmin)
        editor.apply()
    }

    // ✅ Simpan userId setelah login
    fun saveUserId(userId: Int) {
        if (!this::editor.isInitialized) {
            Log.e("SessionManager", "Editor not initialized. Call init(context) first.")
            return
        }
        editor.putInt(KEY_USER_ID, userId)
        editor.apply()
    }

    // ✅ Ambil userId saat perlu akses data user tertentu
    fun getUserId(): Int {
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "Prefs not initialized. Call init(context) first.")
            return -1
        }
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun isLoggedIn(): Boolean {
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "SessionManager not initialized. Call init(context) first.")
            return false
        }
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getAuthToken(): String? {
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "SessionManager not initialized. Call init(context) first.")
            return null
        }
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun isAdmin(): Boolean {
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "SessionManager not initialized. Call init(context) first.")
            return false
        }
        return prefs.getBoolean(KEY_IS_ADMIN, false)
    }

    fun logoutUser() {
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "SessionManager not initialized. Cannot logout.")
            return
        }
        editor.clear()
        editor.apply()
        Log.d("SessionManager", "User logged out. SharedPreferences cleared.")
    }
}
