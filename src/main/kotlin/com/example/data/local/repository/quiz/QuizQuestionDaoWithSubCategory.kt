package com.example.data.local.repository.quiz

import com.example.domain.model.quiz.QuizQuestion
import com.example.domain.model.quiz.QuizQuestionWithSubCategory

interface QuizQuestionDaoWithSubCategory {
    suspend fun insert(
        categoryId: Long,
        categoryTitle: String,
        title: String,
        answer1: String,
        answer2: String,
        answer3: String,
        answer4: String,
        correctAnswer: String
    ): QuizQuestionWithSubCategory?

    suspend fun getAllQuestions(): List<QuizQuestionWithSubCategory>?
    suspend fun getQuizByCategoryId(categoryId: Long): List<QuizQuestionWithSubCategory>?
    suspend fun getQuizById(Id: Long): QuizQuestionWithSubCategory?
    suspend fun deleteQuizById(id: Long): Int
    suspend fun updateQuiz(
        id: Long,
        categoryId: Long,
        categoryTitle: String,
        title: String,
        answer1: String,
        answer2: String,
        answer3: String,
        answer4: String,
        correctAnswer: String
    ): Int
}