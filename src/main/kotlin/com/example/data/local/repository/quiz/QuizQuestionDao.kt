package com.example.data.local.repository.quiz

import com.example.domain.model.quiz.QuizQuestion

interface QuizQuestionDao {
    suspend fun insert(
        categoryId: Long,
        categoryTitle: String,
        title: String,
        answer1: String,
        answer2: String,
        answer3: String,
        answer4: String,
        correctAnswer: String
    ): QuizQuestion?

    suspend fun getAllQuestions(): List<QuizQuestion>?
    suspend fun getQuizByCategoryId(categoryId: Long): List<QuizQuestion>?
    suspend fun getQuizById(Id: Long): QuizQuestion?
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