package com.example.passwordmanager.presentation.stateholders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.data.model.PasswordItem
import com.example.passwordmanager.data.repositories.DatabaseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface PasswordsListViewModelInterface {

    fun getPasswordsList()

}

class PasswordsListViewModel(private val repository: DatabaseRepository) : ViewModel(),
    PasswordsListViewModelInterface {

    private val _passwordsList: MutableStateFlow<List<PasswordItem>> = MutableStateFlow(emptyList())
    val passwordsList: StateFlow<List<PasswordItem>> = _passwordsList

    init {
        getPasswordsList()
    }

    override fun getPasswordsList() {
        viewModelScope.launch {
            repository.loadPasswords().collect() { list ->
                _passwordsList.value = list
            }
        }
    }
}