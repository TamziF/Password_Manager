package com.example.passwordmanager.ioc

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passwordmanager.data.repositories.DatabaseRepository
import com.example.passwordmanager.data.repositories.NetworkRepository
import com.example.passwordmanager.presentation.stateholders.PasswordEditViewModel
import com.example.passwordmanager.presentation.stateholders.PasswordsListViewModel

class ViewModelFactory(
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        PasswordsListViewModel::class.java -> PasswordsListViewModel(databaseRepository)
        PasswordEditViewModel::class.java -> { Log.v("GES-21", "VIEW"); PasswordEditViewModel(networkRepository, databaseRepository) }
        else -> throw IllegalArgumentException()
    } as T
}