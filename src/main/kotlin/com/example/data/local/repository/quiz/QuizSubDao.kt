package com.example.data.local.repository.quiz

import com.example.domain.model.quiz.QuizCategory
import com.example.domain.model.quiz.QuizSubCategory

interface QuizSubDao {
    suspend fun insert(
        categoryName: String,
        name: String,
        description: String,
        imageUrl: String
    ): QuizSubCategory?

    suspend fun getAllSubCategories(): List<QuizSubCategory>?
    suspend fun getSubCategoryById(id: Long): QuizSubCategory?
    suspend fun deleteSubCategoryById(id: Long): Int
    suspend fun updateSubCategory(
        id: Long,
        categoryName: String,
        name: String,
        description: String,
        imageUrl: String,
    ): Int
}
