package com.example.domain.model.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizSubCategory(
    val id: Long,
    val categoryName: String,
    val name: String,
    val description: String,
    val imageUrl: String
)
