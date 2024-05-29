package com.example.domain.repository.papers

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.boards.BoardsDao
import com.example.data.local.table.papers.boards.BoardsTable
import com.example.domain.model.papers.boards.Boards
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class BoardsRepository : BoardsDao {
    override suspend fun insert(title: String, description: String, imageUrl: String): Boards? {
        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = BoardsTable.insert { board ->
                board[BoardsTable.title] = title
                board[BoardsTable.description] = description
                board[BoardsTable.imageUrl] = imageUrl
            }
        }
        return insertStatement?.resultedValues?.get(0)?.let { rowToResult(it) }
    }

    override suspend fun getAllBoards(): List<Boards>? {
        return DatabaseFactory.dbQuery {
            BoardsTable.selectAll()
                .mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getBoardById(id: Long): Boards? {
        return DatabaseFactory.dbQuery {
            BoardsTable.select { BoardsTable.id.eq(id) }
                .mapNotNull { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteBoardById(id: Long): Int {
        return DatabaseFactory.dbQuery {
            BoardsTable.deleteWhere { BoardsTable.id.eq(id) }
        }
    }

    override suspend fun updateBoard(id: Long, title: String, description: String, imageUrl: String): Int {
        return DatabaseFactory.dbQuery {
            BoardsTable.update({ BoardsTable.id.eq(id) }) { board ->
                board[BoardsTable.title] = title
                board[BoardsTable.description] = description
                board[BoardsTable.imageUrl] = imageUrl
            }
        }
    }

    private fun rowToResult(row: ResultRow): Boards? {
        return Boards(
            id = row[BoardsTable.id],
            title = row[BoardsTable.title],
            description = row[BoardsTable.description],
            imageUrl = row[BoardsTable.imageUrl]
        )
    }
}