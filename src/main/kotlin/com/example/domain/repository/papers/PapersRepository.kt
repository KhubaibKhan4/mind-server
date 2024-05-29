package com.example.domain.repository.papers

import com.example.data.local.repository.boards.PapersDao
import com.example.data.local.table.papers.papers.PapersTable
import com.example.domain.model.papers.papers.Papers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class PapersRepository : PapersDao {
    override fun insert(boardId: Long, classId: Long, subjectId: Long, pdfUrl: String): Papers? {
        var paperId: Long? = null
        transaction {
            paperId = PapersTable.insertAndGetId {
                it[PapersTable.boardId] = boardId
                it[PapersTable.classId] = classId
                it[PapersTable.subjectId] = subjectId
                it[PapersTable.pdfUrl] = pdfUrl
            }.value
        }
        return paperId?.let { getPaperById(it) }
    }

    override fun getPapersByBoardId(boardId: Long): List<Papers>? {
        return transaction {
            PapersTable.select { PapersTable.boardId eq boardId }.mapNotNull { toPaper(it) }
        }
    }

    override fun getPapersByClassId(classId: Long): List<Papers>? {
        return transaction {
            PapersTable.select { PapersTable.classId eq classId }.mapNotNull { toPaper(it) }
        }
    }

    override fun getPapersBySubjectId(subjectId: Long): List<Papers>? {
        return transaction {
            PapersTable.select { PapersTable.subjectId eq subjectId }.mapNotNull { toPaper(it) }
        }
    }

    fun update(paperId: Long, boardId: Long?, classId: Long?, subjectId: Long?, pdfUrl: String?): Papers? {
        transaction {
            PapersTable.update({ PapersTable.id eq paperId }) {
                if (boardId != null) it[PapersTable.boardId] = boardId
                if (classId != null) it[PapersTable.classId] = classId
                if (subjectId != null) it[PapersTable.subjectId] = subjectId
                if (pdfUrl != null) it[PapersTable.pdfUrl] = pdfUrl
            }
        }
        return getPaperById(paperId)
    }

    private fun toPaper(row: ResultRow): Papers {
        return Papers(
            id = row[PapersTable.id].value,
            boardId = row[PapersTable.boardId],
            classId = row[PapersTable.classId],
            subjectId = row[PapersTable.subjectId],
            pdfUrl = row[PapersTable.pdfUrl]
        )
    }

    private fun getPaperById(id: Long): Papers? {
        return transaction {
            PapersTable.select { PapersTable.id eq id }.mapNotNull { toPaper(it) }.singleOrNull()
        }
    }
}
