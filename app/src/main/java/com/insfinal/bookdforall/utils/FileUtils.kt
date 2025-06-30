package com.insfinal.bookdforall.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.provider.OpenableColumns // Tambahkan ini

object FileUtils {
    // Helper sederhana untuk mendapatkan File dari Uri.
    // Ini menyalin konten URI ke file cache.
    fun fromUri(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}")
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error, e.g., throw IOException or return null
        }
        return file
    }

    // Helper untuk mendapatkan nama file dari Uri
    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }
}