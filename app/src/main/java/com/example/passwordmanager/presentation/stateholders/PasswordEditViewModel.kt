package com.example.passwordmanager.presentation.stateholders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.data.repositories.DatabaseRepository
import com.example.passwordmanager.data.repositories.NetworkRepository
import com.example.passwordmanager.data.model.PasswordItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface PasswordViewModelInterface {

    fun setItem(id: Int)

    fun deleteItem()

    fun saveItem()

    fun loadIcon()

    fun setUrl(url: String)

    fun setLogin(login: String)

    fun setPassword(password: String)

}

enum class IconState { LOADING, ERROR, DONE }

enum class SavingState { SAVING, SAVED, DEFAULT }

class PasswordEditViewModel(
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel(), PasswordViewModelInterface {

    private val _passwordItem: MutableStateFlow<PasswordItem> =
        MutableStateFlow(PasswordItem("", "", "", ""))
    val passwordItem: StateFlow<PasswordItem> = _passwordItem

    private val _iconUrl: MutableStateFlow<String> = MutableStateFlow("")
    val iconUrl: StateFlow<String> = _iconUrl

    private val _iconState: MutableStateFlow<IconState> = MutableStateFlow(IconState.ERROR)
    val iconState: StateFlow<IconState> = _iconState

    private val _buttonsEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val buttonsEnabled: StateFlow<Boolean> = _buttonsEnabled

    private val _isNewItem: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isNewItem: StateFlow<Boolean> = _isNewItem

    private val _savingState: MutableStateFlow<SavingState> = MutableStateFlow(SavingState.DEFAULT)
    val savingState: StateFlow<SavingState> = _savingState

    override fun setItem(id: Int) {
        viewModelScope.launch {
            _passwordItem.value = databaseRepository.getItem(id)
            _isNewItem.value = false
        }
    }

    override fun deleteItem() {
        viewModelScope.launch {
            databaseRepository.deleteItem(_passwordItem.value)
        }
    }

    private fun updateItem() {
        _passwordItem.value.imageUrl = _iconUrl.value
        _savingState.value = SavingState.SAVING
        viewModelScope.launch {
            databaseRepository.updateItem(_passwordItem.value)
        }.invokeOnCompletion {
            _savingState.value = SavingState.SAVED
        }
    }

    private fun addNewItem() {
        viewModelScope.launch {
            databaseRepository.addItem(
                PasswordItem(
                    imageUrl = _iconUrl.value,
                    login = _passwordItem.value.login,
                    password = _passwordItem.value.password,
                    url = _passwordItem.value.url
                )
            )
        }.invokeOnCompletion {
            _savingState.value = SavingState.SAVED
        }
    }

    override fun saveItem() {
        _buttonsEnabled.value = false
        _savingState.value = SavingState.SAVING
        if (_isNewItem.value) {
            _isNewItem.value = false
            addNewItem()
        }
        else
            updateItem()
    }

    private var job: Job? = null

    override fun loadIcon() {
        job?.cancel()
        _iconState.value = IconState.LOADING
        job = viewModelScope.launch {
            delay(1000L)
            val requestAnswer = networkRepository.loadPhoto(_passwordItem.value.url)
            if (requestAnswer.isSuccessful) {
                if (requestAnswer.body()?.icons?.isEmpty() == true) {
                    _iconUrl.value = ""
                    _iconState.value = IconState.ERROR
                } else {
                    _iconUrl.value = requestAnswer.body()?.icons?.get(0)?.url
                        ?: ""

                    _iconState.value = IconState.DONE
                }
            } else {
                _iconState.value = IconState.ERROR
                _iconUrl.value = ""
            }
        }
    }

    override fun setUrl(url: String) {
        _passwordItem.value.url = url
        checkButtonState()
    }

    override fun setLogin(login: String) {
        _passwordItem.value.login = login
        checkButtonState()
    }

    override fun setPassword(password: String) {
        _passwordItem.value.password = password
        checkButtonState()
    }

    private fun checkButtonState() {
        _buttonsEnabled.value = _passwordItem.value.url.isNotEmpty()
                && _passwordItem.value.login.isNotEmpty()
                && _passwordItem.value.password.isNotEmpty()
    }
}