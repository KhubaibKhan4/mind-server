package com.example.domain.model.papers.papers

import kotlinx.serialization.Serializable

@Serializable
data class Papers(
    val id: Long,
    val boardId: Long,
    val classId: Long,
    val subjectId: Long,
    val pdfUrl: String
)