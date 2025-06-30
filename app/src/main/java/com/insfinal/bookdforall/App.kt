package com.insfinal.bookdforall

import android.app.Application
import com.insfinal.bookdforall.utils.SessionManager // <--- PERBAIKAN: Ubah 'session' menjadi 'utils'

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(applicationContext) // Ini akan memanggil init() pada object SessionManager
    }
}