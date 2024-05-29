package com.example.domain.repository.classes

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.boards.ClassesDao
import com.example.data.local.table.papers.classes.ClassesTable
import com.example.domain.model.papers.classes.BoardClasses
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class ClassesRepository : ClassesDao {
    override suspend fun insert(
        boardId: Long,
        title: String,
        description: String
    ): BoardClasses? {
        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = ClassesTable.insert {
                it[ClassesTable.boardId] = boardId
                it[ClassesTable.title] = title
                it[ClassesTable.description] = description
            }
        }
        return rowToResult(insertStatement?.resultedValues?.get(0)!!)
    }

    override suspend fun getAllClasses(): List<BoardClasses>? {
        return DatabaseFactory.dbQuery {
            ClassesTable.selectAll()
                .mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getClassesById(id: Long): BoardClasses? {
        return DatabaseFactory.dbQuery {
            ClassesTable.select { ClassesTable.id eq id }
                .mapNotNull { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun getClassesByBoardId(id: Long): List<BoardClasses>? {
        return DatabaseFactory.dbQuery {
            ClassesTable.select { ClassesTable.boardId eq id }
                .mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun deleteBoardById(id: Long): Int {
        return DatabaseFactory.dbQuery {
            ClassesTable.deleteWhere { ClassesTable.id eq id }
        }
    }

    override suspend fun updateBoard(
        id: Long,
        boardId: Long,
        title: String,
        description: String
    ): Int {
        return DatabaseFactory.dbQuery {
            ClassesTable.update({ ClassesTable.id eq id }) {
                it[ClassesTable.boardId] = boardId
                it[ClassesTable.title] = title
                it[this.description] = description
            }
        }
    }

    private fun rowToResult(row: ResultRow): BoardClasses? {
        return BoardClasses(
            id = row[ClassesTable.id],
            boardId = row[ClassesTable.boardId],
            title = row[ClassesTable.title],
            description = row[ClassesTable.description]
        )
    }

}
