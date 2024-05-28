package com.example.domain.model.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizQuestion(
    val title: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val answer4: String,
    val correctAnswerIndex: String,
)
