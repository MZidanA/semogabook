package com.insfinal.bookdforall.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.insfinal.bookdforall.databinding.DialogPublisherFormBinding
import com.insfinal.bookdforall.model.Publisher

class PublisherFormDialog(
    context: Context,
    private val existingPublisher: Publisher?,
    private val onSubmit: (Publisher) -> Unit
) {
    private val dialog: AlertDialog

    init {
        val binding = DialogPublisherFormBinding.inflate(LayoutInflater.from(context))
        binding.etNama.setText(existingPublisher?.nama_penerbit ?: "")
        binding.etAlamat.setText(existingPublisher?.alamat ?: "")
        binding.etKontak.setText(existingPublisher?.kontak ?: "")

        dialog = AlertDialog.Builder(context)
            .setTitle(if (existingPublisher == null) "Tambah Penerbit" else "Edit Penerbit")
            .setView(binding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val publisher = Publisher(
                    publisherId = existingPublisher?.publisherId ?: 0,
                    nama_penerbit = binding.etNama.text.toString(),
                    alamat = binding.etAlamat.text.toString(),
                    kontak = binding.etKontak.text.toString()
                )
                onSubmit(publisher)
            }
            .setNegativeButton("Batal", null)
            .create()
    }

    fun show() = dialog.show()
}
