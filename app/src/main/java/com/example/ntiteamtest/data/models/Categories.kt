package com.example.ntiteamtest.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Categories(
    val id: Int,
    val name: String
)
