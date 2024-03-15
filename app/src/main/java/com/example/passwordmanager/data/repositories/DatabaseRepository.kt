package com.example.passwordmanager.data.repositories

import com.example.passwordmanager.data.database.Dao
import com.example.passwordmanager.data.model.PasswordItem
import kotlinx.coroutines.flow.Flow

interface DatabaseRepositoryInterface {

    fun loadPasswords(): Flow<List<PasswordItem>>

    suspend fun addItem(item: PasswordItem)

    suspend fun updateItem(item: PasswordItem)

    suspend fun deleteItem(item: PasswordItem)

    suspend fun getItem(id: Int): PasswordItem
}

class DatabaseRepository(
    private val dao: Dao
) : DatabaseRepositoryInterface {

    override fun loadPasswords(): Flow<List<PasswordItem>> {
        return dao.loadPasswords()
    }

    override suspend fun addItem(item: PasswordItem) {
        return dao.addItem(item)
    }

    override suspend fun updateItem(item: PasswordItem) {
        return dao.updateItem(item)
    }

    override suspend fun deleteItem(item: PasswordItem) {
        return dao.deletePassword(item)
    }

    override suspend fun getItem(id: Int): PasswordItem {
        return dao.getItem(id)
    }
}