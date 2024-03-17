package com.example.passwordmanager.ioc.passwordsList

import android.content.Context
import com.example.passwordmanager.presentation.stateholders.PasswordsListViewModel
import com.example.passwordmanager.presentation.view.passwordsList.PasswordsListFragment

class PasswordsListFragmentComponent(
    val viewModel: PasswordsListViewModel,
    val fragment: PasswordsListFragment,
    val context: Context
)