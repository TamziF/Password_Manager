package com.example.passwordmanager.presentation.view.passwordsList.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.passwordmanager.data.model.PasswordItem
import com.example.passwordmanager.databinding.PasswordItemBinding
import com.example.passwordmanager.presentation.stateholders.PasswordsListViewModel
import com.example.passwordmanager.presentation.view.passwordsList.PasswordsListFragment

class PasswordsListAdapter(
    private val fragment: PasswordsListFragment
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