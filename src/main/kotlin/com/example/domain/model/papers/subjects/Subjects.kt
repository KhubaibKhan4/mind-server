package com.example.domain.model.papers.subjects

import kotlinx.serialization.Serializable

@Serializable
data class Subjects(
    val id: Long,
    val boardId: Long,
    val classId: Long,
    val title: String,
    val description: String,
)

