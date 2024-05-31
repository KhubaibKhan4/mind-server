package com.example.domain.model.promotion

import kotlinx.serialization.Serializable

@Serializable
data class Promotions(
    val id:Long,
    val imageUrl: String
)
