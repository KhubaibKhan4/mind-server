package com.example.data.local.repository.quiz

import com.example.domain.model.quiz.QuizCategory

interface QuizDao {
    suspend fun insert(
        name: String,
        description: String,
        imageUrl: String
    ): QuizCategory?

    suspend fun getAllCategories(): List<QuizCategory>?
    suspend fun getCategoryById(id: Long): QuizCategory?
    suspend fun deleteCategoryById(id: Long): Int
    suspend fun updateCategory(
        id: Long,
        name: String,
        description: String,
        imageUrl: String,
    ): Int
}