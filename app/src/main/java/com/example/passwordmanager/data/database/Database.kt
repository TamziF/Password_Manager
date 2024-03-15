package com.example.passwordmanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.passwordmanager.data.model.PasswordItem

@Database(version = 1, entities = [PasswordItem::class])
abstract class Database: RoomDatabase() {
    abstract fun dao(): Dao
}