package com.example.passwordmanager.presentation.view.passwordsList

import android.content.Context
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.databinding.FragmentPasswordsListBinding
import com.example.passwordmanager.presentation.stateholders.PasswordsListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PasswordsListFragmentViewController(
    private val fragment: PasswordsListFragment,
    private val viewModel: PasswordsListViewModel,
    private val context: Context,
    binding: FragmentPasswordsListBinding
) {

    private val recyclerView = binding.recyclerView
    private val addButton = binding.addButton

    fun bindViews() {
        bindRecycler()
        bindAddButton()
    }

    private fun bindRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = PasswordsListAdapter(fragment, viewModel)
        recyclerView.adapter = adapter
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            viewModel.passwordsList.collect { list ->
                adapter.submitList(list)
            }
        }
    }

    private fun bindAddButton() {
        addButton.setOnClickListener {
            val direction =
                PasswordsListFragmentDirections.actionPasswordsListFragmentToPasswordEditFragment()
            fragment.findNavController().navigate(direction)
        }
    }

}