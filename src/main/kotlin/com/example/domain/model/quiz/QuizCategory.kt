package com.example.domain.model.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizCategory(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String
)
