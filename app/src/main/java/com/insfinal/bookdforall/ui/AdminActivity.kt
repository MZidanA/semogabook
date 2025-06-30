package com.insfinal.bookdforall.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.insfinal.bookdforall.R
import com.insfinal.bookdforall.databinding.ActivityAdminBinding
import com.insfinal.bookdforall.utils.SessionManager

// Kelas AdminActivity adalah titik masuk utama untuk fungsionalitas admin.
class AdminActivity : AppCompatActivity() {

    // Binding untuk mengakses elemen-elemen UI dari layout activity_admin.xml.
    private lateinit var binding: ActivityAdminBinding

    // Dipanggil saat aktivitas pertama kali dibuat.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginisialisasi binding dengan mengembang (inflate) layout.
        binding = ActivityAdminBinding.inflate(layoutInflater)
        // Mengatur tampilan konten aktivitas.
        setContentView(binding.root)

        // Mengatur ActionBar untuk AdminActivity dengan judul default dan tanpa tombol kembali.
        setupToolbar("Admin Dashboard", false) // false berarti tidak ada tombol kembali di awal.

        // Keadaan awal: menampilkan tampilan dashboard, menyembunyikan container fragmen.
        showDashboardView()

        // Listener untuk tombol "Kelola Pengguna".
        binding.btnKelolaPengguna.setOnClickListener {
            // Memuat UserManageFragment ke dalam container.
            loadFragment(UserManageFragment(), "UserManageFragment")
            setupToolbar("Kelola Pengguna", true) // Menampilkan tombol kembali.
        }

        // Listener untuk tombol "Kelola Buku".
        binding.btnKelolaBuku.setOnClickListener {
            loadFragment(AdminBookListFragment(), "AdminBookListFragment") // Menggunakan AdminBookListFragment.
            setupToolbar("Kelola Buku", true) // Menampilkan tombol kembali.
        }
        // Listener untuk tombol "Kelola Publisher".
        binding.btnKelolaPublisher.setOnClickListener {
            loadFragment(PublisherManageFragment(), "PublisherManageFragment")
            setupToolbar("Kelola Penerbit", true)
        }

        // Listener untuk tombol "Logout Admin".
        binding.btnAdminLogout.setOnClickListener {
            SessionManager.logoutUser() // Melakukan logout pengguna admin.
            Toast.makeText(this, "Admin Logout Berhasil!", Toast.LENGTH_SHORT).show()
            // Memulai LoginActivity dan mengosongkan tumpukan aktivitas.
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish() // Menutup AdminActivity.
        }

        // Menangani fragmen awal jika datang dari pemulihan status atau deep link.
        if (savedInstanceState == null) {
            // Opsional: memuat fragmen default di sini jika Anda tidak ingin menampilkan dashboard terlebih dahulu
            // atau melakukan pemuatan data awal untuk ringkasan dashboard.
        }
    }

    // Mengatur toolbar (ActionBar) dengan judul dan visibilitas tombol kembali.
    private fun setupToolbar(title: String, showBackButton: Boolean) {
        supportActionBar?.title = title // Mengatur judul ActionBar.
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton) // Mengatur visibilitas tombol kembali.
    }

    // Menampilkan tampilan dashboard dan menyembunyikan container fragmen.
    private fun showDashboardView() {
        binding.adminDashboardScrollView.visibility = View.VISIBLE
        binding.adminFragmentContainer.visibility = View.GONE
    }

    // Menyembunyikan tampilan dashboard dan menampilkan container fragmen.
    private fun showFragmentContainer() {
        binding.adminDashboardScrollView.visibility = View.GONE
        binding.adminFragmentContainer.visibility = View.VISIBLE
    }

    // Fungsi pembantu untuk memuat fragmen ke dalam container.
    private fun loadFragment(fragment: Fragment, tag: String) {
        showFragmentContainer() // Memastikan container terlihat.

        supportFragmentManager.beginTransaction()
            .replace(R.id.admin_fragment_container, fragment, tag) // Mengganti fragmen di container.
            .addToBackStack(tag) // Menambahkan transaksi ke back stack dengan tag.
            .commit() // Melakukan transaksi fragmen.
    }

    // Menangani penekanan tombol kembali.
    override fun onBackPressed() {
        // Jika ada lebih dari satu entri di back stack (berarti ada fragmen yang dimuat).
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack() // Mengeluarkan fragmen dari back stack.
            // Memeriksa fragmen saat ini setelah pop.
            val currentFragment = supportFragmentManager.findFragmentById(R.id.admin_fragment_container)
            // Mengatur toolbar berdasarkan fragmen yang sedang ditampilkan.
            if (currentFragment is UserManageFragment) {
                setupToolbar("Kelola Pengguna", true)
            } else if (currentFragment is AdminBookListFragment) {
                setupToolbar("Kelola Buku", true)
            } else {
                setupToolbar("Admin Dashboard", false) // Default jika tidak ada fragmen spesifik.
                showDashboardView() // Menampilkan konten dashboard.
            }
        } else if (supportFragmentManager.backStackEntryCount == 1) { // Hanya satu fragmen tersisa, pop dan tampilkan dashboard.
            supportFragmentManager.popBackStack()
            setupToolbar("Admin Dashboard", false)
            showDashboardView()
        } else {
            super.onBackPressed() // Melakukan perilaku kembali default (keluar aplikasi).
        }
    }

    // Menangani klik tombol kembali di ActionBar.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Memicu tindakan tombol kembali.
        return true
    }
}