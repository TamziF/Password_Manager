package com.example.passwordmanager.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IconResponse(
    val icons: List<Url>
)