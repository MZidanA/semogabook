// UserManageFragment.kt
package com.insfinal.bookdforall.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.insfinal.bookdforall.databinding.FragmentUserManageBinding
import com.insfinal.bookdforall.model.User // User model
import com.insfinal.bookdforall.adapters.AdminUserAdapter
import com.insfinal.bookdforall.ui.UserManageViewModel
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope // Import lifecycleScope
import kotlinx.coroutines.launch

class UserManageFragment : Fragment() {

    private var _binding: FragmentUserManageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserManageViewModel by viewModels()

    private lateinit var adapter: AdminUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        viewModel.loadUsers()

        binding.searchView.addTextChangedListener { editable ->
            viewModel.setSearchQuery(editable.toString())
        }
    }

    private fun setupRecyclerView() {
        val adminAdapter = AdminUserAdapter(
            onDeleteClick = { user ->
                showDeleteConfirmationDialog(user)
            }
        )
        adapter = adminAdapter
        binding.rvAdminUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminUsers.adapter = adminAdapter
    }



    private fun observeViewModel() {
        viewModel.filteredUsers.observe(viewLifecycleOwner) { userList ->
            Log.d("UserManageFragment", "Filtered user list: ${userList.size}")
            adapter.submitList(userList)
            binding.emptyUserView.visibility = if (userList.isEmpty()) View.VISIBLE else View.GONE
        }


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventSuccess.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.eventError.collect { message ->
                Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                Log.e("UserManageFragment", "Error: $message")
            }
        }
    }

    private fun showDeleteConfirmationDialog(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Pengguna")
            .setMessage("Apakah Anda yakin ingin menghapus pengguna '${user.nama}' (${user.email})?")
            .setPositiveButton("Hapus") { dialog, which ->
                viewModel.deleteUser(user.userId) // <--- PERBAIKAN DI SINI: Gunakan userId
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}