package com.example.domain.repository.papers

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.boards.SubjectsDao
import com.example.data.local.table.papers.subjects.SubjectsTable
import com.example.domain.model.papers.subjects.Subjects
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction

class SubjectsRepository : SubjectsDao {
    override suspend fun insert(boardId: Long, classId: Long, title: String, description: String): Subjects? {
        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = SubjectsTable.insert {
                it[SubjectsTable.boardId] = boardId
                it[SubjectsTable.classId] = classId
                it[SubjectsTable.title] = title
                it[SubjectsTable.description] = description
            }
        }
        return rowToResult(insertStatement?.resultedValues?.get(0)!!)
    }

    override suspend fun getAllSubjects(): List<Subjects>? {
        return DatabaseFactory.dbQuery {
            SubjectsTable.selectAll()
                .mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getSubjectById(id: Long): Subjects? {
        return DatabaseFactory.dbQuery {
            SubjectsTable.select { SubjectsTable.id eq id }
                .mapNotNull { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun getSubjectsByBoardId(boardId: Long): List<Subjects>? {
        return DatabaseFactory.dbQuery {
            SubjectsTable.select { SubjectsTable.boardId eq boardId }
                .mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getSubjectsByClassId(classId: Long): List<Subjects>? {
        return DatabaseFactory.dbQuery {
            SubjectsTable.select { SubjectsTable.classId eq classId }
                .mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun deleteSubjectById(id: Long): Int {
        return DatabaseFactory.dbQuery {
            SubjectsTable.deleteWhere { SubjectsTable.id eq id }
        }
    }

    override suspend fun updateSubject(id: Long, boardId: Long, classId: Long, title: String, description: String): Int {
        return DatabaseFactory.dbQuery {
            SubjectsTable.update({ SubjectsTable.id eq id }) {
                it[SubjectsTable.boardId] = boardId
                it[SubjectsTable.classId] = classId
                it[SubjectsTable.title] = title
                it[SubjectsTable.description] = description
            }
        }
    }

    private fun rowToResult(row: ResultRow): Subjects {
        return Subjects(
            id = row[SubjectsTable.id],
            boardId = row[SubjectsTable.boardId],
            classId = row[SubjectsTable.classId],
            title = row[SubjectsTable.title],
            description = row[SubjectsTable.description]
        )
    }
}
