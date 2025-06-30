// PublisherManageFragment.kt
package com.insfinal.bookdforall.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.insfinal.bookdforall.databinding.FragmentPublisherManageBinding
import com.insfinal.bookdforall.model.Publisher
import com.insfinal.bookdforall.adapters.PublisherAdapter
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PublisherManageFragment : Fragment() {

    private var _binding: FragmentPublisherManageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PublisherViewModel by viewModels()
    private lateinit var adapter: PublisherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublisherManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        viewModel.loadPublishers()

        binding.btnAddPublisher.setOnClickListener {
            showAddDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = PublisherAdapter(
            onDeleteClick = { publisher ->
                showDeleteDialog(publisher)
            },
            onEditClick = { publisher ->
                showEditDialog(publisher)
            }
        )
        binding.rvPublishers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPublishers.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.publishers.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyPublisherView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        lifecycleScope.launch {
            viewModel.eventError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.eventSuccess.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddDialog() {
        val dialog = PublisherFormDialog(requireContext(), null) { publisher ->
            viewModel.createPublisher(publisher)
        }
        dialog.show()
    }

    private fun showEditDialog(publisher: Publisher) {
        val dialog = PublisherFormDialog(requireContext(), publisher) { updatedPublisher ->
            viewModel.updatePublisher(publisher.publisherId, updatedPublisher)
        }
        dialog.show()
    }

    private fun showDeleteDialog(publisher: Publisher) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Penerbit")
            .setMessage("Yakin ingin menghapus '${publisher.nama_penerbit}'?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deletePublisher(publisher.publisherId)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
