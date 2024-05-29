package com.example.domain.model.papers.combine

import com.example.domain.model.papers.subjects.Subjects
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
    val subjects: List<Subjects> = emptyList()
)