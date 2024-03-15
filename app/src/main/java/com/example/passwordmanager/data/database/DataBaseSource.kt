package com.example.passwordmanager.data.database

import android.content.Context
import androidx.room.Room

class DataBaseSource(context: Context) {
    private val database = Room
        .databaseBuilder(
            context,
            Database::class.java,
            "PasswordsDatabase"
        )
        .build()

    val dao = database.dao()
}