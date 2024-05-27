package com.example.data.local.repository.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizCategory(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String
)
