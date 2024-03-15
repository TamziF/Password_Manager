package com.example.passwordmanager.presentation.view.passwordsList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.data.model.PasswordItem
import com.example.passwordmanager.databinding.PasswordItemBinding
import com.example.passwordmanager.presentation.stateholders.PasswordsListViewModel

class PasswordsListAdapter(
    private val fragment: PasswordsListFragment,
    private val viewModel: PasswordsListViewModel
): ListAdapter<PasswordItem, PasswordItemViewHolder>(
    PasswordItemDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordItemViewHolder {
        val binding = PasswordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PasswordItemViewHolder(binding, fragment)
    }

    override fun onBindViewHolder(holder: PasswordItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}