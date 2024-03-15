package com.example.passwordmanager.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.passwordmanager.data.model.PasswordItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query("SELECT * FROM Passwords")
    fun loadPasswords(): Flow<List<PasswordItem>>

    @Insert
    suspend fun addItem(item: PasswordItem)

    @Update
    suspend fun updateItem(item: PasswordItem)

    @Delete
    suspend fun deletePassword(item: PasswordItem)

    @Query("SELECT * FROM Passwords WHERE id = :id")
    suspend fun getItem(id: Int): PasswordItem
}