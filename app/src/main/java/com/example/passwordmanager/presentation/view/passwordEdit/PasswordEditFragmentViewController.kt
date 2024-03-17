package com.example.passwordmanager.presentation.view.passwordEdit

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.passwordmanager.AccessBottomSheet
import com.example.passwordmanager.PasswordManagerApp
import com.example.passwordmanager.R
import com.example.passwordmanager.databinding.FragmentPasswordEditBinding
import com.example.passwordmanager.presentation.stateholders.IconState
import com.example.passwordmanager.presentation.stateholders.PasswordEditViewModel
import com.example.passwordmanager.presentation.stateholders.SavingState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class PasswordEditFragmentViewController(
    private val viewModel: PasswordEditViewModel,
    private val fragment: PasswordEditFragment,
    binding: FragmentPasswordEditBinding,
    private val imageLoader: ImageLoader
) {
    private val image = binding.image
    private val urlEt = binding.url
    private val loginEt = binding.login
    private val password = binding.password
    private val passwordEt = binding.passwordEt
    private val saveButton = binding.saveButton
    private val deleteButton = binding.deleteButton
    private val backImage = binding.backImage
    private val savingStateImage = binding.savingStateImage

    fun bindViews() {

        setFromViewModelState()

        bindUrlEditText()
        bindLoginEditText()
        bindPasswordEditText()

        bindIconImage()

        bindSaveButton()
        bindDeleteButton()
        bindBackImage()

        bindOldItemLoad()

        bindSavingStateImage()

        bindBottomSheetBehavior()
    }

    private fun bindBottomSheetBehavior() {
        passwordEt.setEndIconOnClickListener {
            val inputType = password.inputType
            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Текущее состояние пароля - показан
                password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                Log.d("previousIcon", "Close") // Вывести "close" в Log
            } else {
                // Текущее состояние пароля - скрыт
                val bottomSheet = AccessBottomSheet()
                bottomSheet.show(fragment.parentFragmentManager, AccessBottomSheet.TAG)
                fragment.viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.isAuthorized.collect { isAuthorized ->
                        if (isAuthorized) {
                            password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            bottomSheet.dismiss()
                        }
                    }
                }
                Log.d("previousIcon", "Show") // Вывести "show" в Log
            }
        }
    }

    private fun bindSavingStateImage() {
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savingState.collect { state ->
                    savingStateImage.visibility = View.VISIBLE
                    when (state) {
                        SavingState.SAVING -> {
                            savingStateImage.setImageResource(R.drawable.save)
                        }

                        SavingState.SAVED -> {
                            savingStateImage.setImageResource(R.drawable.download_done)
                        }

                        SavingState.DEFAULT -> {
                            savingStateImage.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun bindOldItemLoad() {
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isNewItem.collect { isNewItem ->
                    if (!isNewItem) {
                        setFromViewModelState()
                        this.cancel()
                    }
                }
            }
        }
    }

    private fun bindBackImage() {
        backImage.setOnClickListener {
            val direction =
                PasswordEditFragmentDirections.actionPasswordEditFragmentToPasswordsListFragment()
            fragment.findNavController().navigate(direction)
        }
    }

    private fun bindDeleteButton() {

        fragment.viewLifecycleOwner.lifecycleScope.launch {
            fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.buttonsEnabled.collect { isEnabled ->
                    deleteButton.isEnabled = isEnabled
                }
            }
        }

        deleteButton.setOnClickListener {
            viewModel.deleteItem()
            val direction =
                PasswordEditFragmentDirections.actionPasswordEditFragmentToPasswordsListFragment()
            fragment.findNavController().navigate(direction)
        }
    }

    private fun bindSaveButton() {
        saveButton.setOnClickListener {
            viewModel.saveItem()
        }

        fragment.viewLifecycleOwner.lifecycleScope.launch {
            fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.buttonsEnabled.collect { isEnabled ->
                    saveButton.isEnabled = isEnabled
                }
            }
        }
    }

    private fun setFromViewModelState() {
        urlEt.setText(viewModel.passwordItem.value.url)
        loginEt.setText(viewModel.passwordItem.value.login)
        password.setText(viewModel.passwordItem.value.password)
    }

    private fun bindLoginEditText() {

        loginEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setLogin(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    private fun bindPasswordEditText() {

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setPassword(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    private fun bindUrlEditText() {

        urlEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setUrl(s.toString())
                viewModel.loadIcon()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    private fun bindIconImage() {

        fragment.viewLifecycleOwner.lifecycleScope.launch {
            fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.iconState.collect { state ->
                    when (state) {

                        IconState.DONE -> {
                            downloadIcon()
                        }

                        IconState.LOADING -> {
                            image.setImageResource(R.drawable.loading)
                        }

                        IconState.ERROR -> {
                            downloadIcon()
                            image.setImageResource(R.drawable.error_image)
                        }
                    }
                }
            }
        }

    }

    private fun downloadIcon() {
        image.load(viewModel.passwordItem.value.imageUrl) {
            Log.d("IMAGE_CACHE", "${viewModel.passwordItem.value.imageUrl} edit")
            error(R.drawable.error_image)
        }
    }
}