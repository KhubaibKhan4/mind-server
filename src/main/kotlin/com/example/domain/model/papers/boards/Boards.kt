package com.example.domain.model.papers.boards

import kotlinx.serialization.Serializable

@Serializable
data class Boards(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl:String
)
