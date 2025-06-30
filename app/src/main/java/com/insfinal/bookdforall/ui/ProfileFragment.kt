package com.insfinal.bookdforall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.insfinal.bookdforall.databinding.FragmentProfileBinding
import com.insfinal.bookdforall.repository.UserRepository
import com.insfinal.bookdforall.utils.SessionManager // <--- IMPORT INI
import kotlinx.coroutines.launch
import android.content.Context // Import Context jika belum ada


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivAvatar.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pastikan Anda memuat gambar avatar dari penyimpanan internal atau Glide jika sudah login sebelumnya
        if (selectedImageUri == null) {
            binding.ivAvatar.setImageURI(
                Uri.parse("android.resource://${requireActivity().packageName}/drawable/profile")
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = UserRepository().getCurrentUser()
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        binding.tvNama.text = user.nama
                        binding.tvEmail.text = user.email
                    }
                } else {
                    Log.e("ProfileFragment", "Gagal dapat user: ${response.code()}")
                    // Jika gagal mendapatkan user, mungkin sesi sudah expired
                    // Anda bisa pertimbangkan untuk logout paksa di sini
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error fetch user", e)
                // Jika ada error jaringan, mungkin juga sesi perlu dicek ulang atau logout
            }
        }

        binding.ivAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnUbahProfil.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.btnUbahPassword.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    // --- PERBAIKAN: Gunakan SessionManager.logoutUser() ---
                    SessionManager.logoutUser() // Ini akan membersihkan SharedPreferences Anda

                    // Arahkan ke LoginActivity dan bersihkan back stack
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    // Tidak perlu finish() di Fragment, karena startActivity akan dijalankan oleh Activity induk
                    // dan Activity induk akan di-finish oleh FLAG_ACTIVITY_CLEAR_TASK jika LoginActivity adalah SingleTop
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}