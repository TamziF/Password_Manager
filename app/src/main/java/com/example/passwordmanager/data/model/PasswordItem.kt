package com.example.passwordmanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "Passwords")
data class PasswordItem(
    @ColumnInfo(name = "image_url")
    var imageUrl: String,
    @ColumnInfo
    var url: String,
    @ColumnInfo
    var login: String,
    @ColumnInfo
    var password: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)