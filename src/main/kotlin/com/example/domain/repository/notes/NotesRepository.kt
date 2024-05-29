package com.example.domain.repository.notes

import com.example.data.local.repository.notes.NotesDao
import com.example.data.local.table.notes.NotesTable
import com.example.domain.model.notes.Note
import org.jetbrains.exposed.sql.ResultRow

class NotesRepository:NotesDao {
    override suspend fun insert(title: String, description: String, pdfUrl: String): Note? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllNotes(): List<Note>? {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteById(id: Long): Note? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNote(id: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateNote(id: Long, title: String, description: String, pdfUrl: String): Int {
        TODO("Not yet implemented")
    }
    private fun rowToResult(row: ResultRow):Note?{
        if (row==null){
            return null
        }else{
            return Note(
                id = row[NotesTable.id],
                title = row[NotesTable.title],
                description = row[NotesTable.description],
                pdfUrl = row[NotesTable.pdfUrl]
            )
        }
    }
}