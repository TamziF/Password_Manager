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
        //getDecadeItems()
    }

    private var decadeCounter = 0

    override fun getPasswordsList() {
        viewModelScope.launch {
            repository.loadPasswords().collect() { list ->
                _passwordsList.value = list
            }
        }
    }

    fun getNewDecadeRequest(currentPos: Int) {
        if ((decadeCounter - 1) * 10 - currentPos <= 4)
            getDecadeItems()
    }

    override fun onCleared() {
        Log.v("VIEWMODELCLEARED", "PIZDEC")
        super.onCleared()
    }

    private var job: Job? = null

    private fun getDecadeItems() {
        if (job == null) {
            job = viewModelScope.launch {
                val newList = repository.getDecadeItems(decadeCounter)
                _passwordsList.value += newList
                decadeCounter++
                throw CancellationException()
            }
        }
    }
}