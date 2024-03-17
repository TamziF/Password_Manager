package com.example.passwordmanager.presentation.view.passwordsList

import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.passwordmanager.R
import com.example.passwordmanager.data.model.PasswordItem
import com.example.passwordmanager.databinding.PasswordItemBinding
import com.example.passwordmanager.presentation.stateholders.PasswordsListViewModel

class PasswordItemViewHolder(
    private val binding: PasswordItemBinding,
    private val fragment: PasswordsListFragment
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PasswordItem) {
        binding.image.load(item.imageUrl) {
            Log.d("IMAGE_CACHE", "${item.imageUrl} vh")
            error(R.drawable.error_image)
        }

        binding.login.text = item.login

        binding.card.setOnClickListener {
            fragment.setFragmentResult(
                R.string.request_key.toString(),
                bundleOf(R.string.item_id.toString() to item.id)
            )

            val direction = PasswordsListFragmentDirections.actionPasswordsListFragmentToPasswordEditFragment()
            it.findNavController().navigate(direction)
        }
    }

}