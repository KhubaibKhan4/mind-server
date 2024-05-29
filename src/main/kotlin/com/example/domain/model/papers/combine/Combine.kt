package com.example.domain.model.papers.combine

import com.example.domain.model.papers.papers.Papers
import kotlinx.serialization.Serializable

@Serializable
data class BoardDetails(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String,
    val classes: List<ClassDetails> = emptyList()
)

@Serializable
data class ClassDetails(
    val id: Long,
    val boardId: Long,
    val title: String,
    val description: String,
    val subjects: List<SubjectDetails> = emptyList()
)

@Serializable
data class SubjectDetails(
    val id: Long,
    val classId: Long,
    val title: String,
    val description: String,
    val papers: List<Papers> = emptyList()
)