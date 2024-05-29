package com.example.domain.repository.notes

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.notes.NotesDao
import com.example.data.local.table.notes.NotesTable
import com.example.domain.model.notes.Note
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class NotesRepository : NotesDao {
    override suspend fun insert(title: String, description: String, pdfUrl: String): Note? {
        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = NotesTable.insert { note ->
                note[NotesTable.title] = title
                note[NotesTable.description] = description
                note[NotesTable.pdfUrl] = pdfUrl
            }
        }
        return rowToResult(insertStatement?.resultedValues?.get(0)!!)
    }

    override suspend fun getAllNotes(): List<Note>? {
        return DatabaseFactory.dbQuery {
            NotesTable.selectAll()
                .mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getNoteById(id: Long): Note? {
        return DatabaseFactory.dbQuery {
            NotesTable.select { NotesTable.id.eq(id) }
                .map { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteNote(id: Long): Int {
        return DatabaseFactory.dbQuery {
            NotesTable.deleteWhere { NotesTable.id.eq(id) }
        }
    }

    override suspend fun updateNote(id: Long, title: String, description: String, pdfUrl: String): Int {
        return DatabaseFactory.dbQuery {
            NotesTable.update({ NotesTable.id.eq(id) }) { note ->
                note[NotesTable.id] = id
                note[NotesTable.title] = title
                note[NotesTable.description]=description
                note[NotesTable.pdfUrl]=pdfUrl
            }
        }
    }

    private fun rowToResult(row: ResultRow): Note? {
        if (row == null) {
            return null
        } else {
            return Note(
                id = row[NotesTable.id],
                title = row[NotesTable.title],
                description = row[NotesTable.description],
                pdfUrl = row[NotesTable.pdfUrl]
            )
        }
    }
}