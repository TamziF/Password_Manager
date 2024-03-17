package com.example.passwordmanager.presentation.stateholders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.CryptoManager
import com.example.passwordmanager.data.model.IconResponse
import com.example.passwordmanager.data.repositories.DatabaseRepository
import com.example.passwordmanager.data.repositories.NetworkRepository
import com.example.passwordmanager.data.model.PasswordItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

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

    private val _iconState: MutableStateFlow<IconState> = MutableStateFlow(IconState.ERROR)
    val iconState: StateFlow<IconState> = _iconState

    private val _buttonsEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val buttonsEnabled: StateFlow<Boolean> = _buttonsEnabled

    private val _isNewItem: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isNewItem: StateFlow<Boolean> = _isNewItem

    private val _savingState: MutableStateFlow<SavingState> = MutableStateFlow(SavingState.DEFAULT)
    val savingState: StateFlow<SavingState> = _savingState

    private val _isAuthorized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized

    fun clearData() {
        _passwordItem.value = PasswordItem("", "", "", "")
        _iconState.value = IconState.ERROR
        _buttonsEnabled.value = false
        _isNewItem.value = true
        _savingState.value = SavingState.DEFAULT
        _isAuthorized.value = false
    }

    fun setAuth(isAuthorized: Boolean) {
        _isAuthorized.value = isAuthorized
    }

    override fun setItem(id: Int) {
        viewModelScope.launch {
            _passwordItem.value = databaseRepository.getItem(id)
            _iconState.value = IconState.DONE
            decryptPassword()
            _isNewItem.value = false
        }
    }

    override fun deleteItem() {
        viewModelScope.launch {
            databaseRepository.deleteItem(_passwordItem.value)
        }
    }

    private fun updateItem() {
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
                    imageUrl = _passwordItem.value.imageUrl,
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
        encryptPassword()
        if (_isNewItem.value) {
            _isNewItem.value = false
            addNewItem()
        } else
            updateItem()
    }

    private fun encryptPassword() {
        _passwordItem.value.password = CryptoManager.encrypt(_passwordItem.value.password)
    }

    private fun decryptPassword() {
        _passwordItem.value.password = CryptoManager.decrypt(_passwordItem.value.password)
    }

    private var job: Job? = null

    override fun loadIcon() {
        job?.cancel()
        _iconState.value = IconState.LOADING
        job = viewModelScope.launch {
            delay(1000L)
            Log.d("POCHEMUTO_PADAET", "before")
            lateinit var requestAnswer: Response<IconResponse>
            try {
                requestAnswer = networkRepository.loadPhoto(_passwordItem.value.url)
            } catch (e: Exception) {
                _iconState.value = IconState.ERROR
                _passwordItem.value.imageUrl = ""
                Log.d("POCHEMUTO_PADAET", "close")
                throw CancellationException()
            }
            Log.d("POCHEMUTO_PADAET", "after")
            if (requestAnswer.isSuccessful) {
                if (requestAnswer.body()?.icons?.isEmpty() == true) {
                    _passwordItem.value.imageUrl = ""
                    _iconState.value = IconState.ERROR
                } else {
                    _passwordItem.value.imageUrl = requestAnswer.body()?.icons?.get(0)?.url
                        ?: ""

                    _iconState.value = IconState.DONE
                }
            } else {
                _iconState.value = IconState.ERROR
                _passwordItem.value.imageUrl = ""
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