package com.example.domain.model.resume

import kotlinx.serialization.Serializable

@Serializable
data class Resume(
    val id: Long,
    val categoryName: String,
    val imageUrl: String
)
