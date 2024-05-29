package com.example.domain.model.papers.classes

import kotlinx.serialization.Serializable

@Serializable
data class BoardClasses(
    val id: Long,
    val boardId: Long,
    val title: String,
    val description: String,
)

