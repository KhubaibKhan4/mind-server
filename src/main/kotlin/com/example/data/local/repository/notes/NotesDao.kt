package com.example.data.local.repository.notes

import com.example.domain.model.notes.Note

interface NotesDao {
    suspend fun insert(
        title: String,
        description: String,
        pdfUrl: String
    ): Note?

    suspend fun getAllNotes(): List<Note>?
    suspend fun getNoteById(id: Long): Note?
    suspend fun deleteNote(id: Long): Int
    suspend fun updateNote(
        id: Long,
        title: String,
        description: String,
        pdfUrl: String,
    ): Int
}