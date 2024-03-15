package com.example.passwordmanager.presentation.view.passwordsList

import androidx.recyclerview.widget.DiffUtil
import com.example.passwordmanager.data.model.PasswordItem

class PasswordItemDiffCallback: DiffUtil.ItemCallback<PasswordItem>() {
    override fun areItemsTheSame(oldItem: PasswordItem, newItem: PasswordItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PasswordItem, newItem: PasswordItem): Boolean {
        return oldItem == newItem
    }
}