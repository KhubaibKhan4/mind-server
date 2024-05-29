package com.example.domain.model.notes

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Long,
    val title: String,
    val description: String,
    val pdfUrl: String,
)
