package com.example.domain.repository.resume

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.resume.ResumeDao
import com.example.data.local.table.resume.ResumeTable
import com.example.domain.model.resume.Resume
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class ResumeRepository : ResumeDao {
    override suspend fun insert(categoryName: String, imageUrl: String): Resume? {
        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = ResumeTable.insert { resume ->
                resume[ResumeTable.categoryName] = categoryName
                resume[ResumeTable.imageUrl] = imageUrl
            }
        }
        return rowToResult(insertStatement?.resultedValues?.get(0)!!)
    }

    override suspend fun getAllResume(): List<Resume>? {
        return DatabaseFactory.dbQuery {
            ResumeTable.selectAll().mapNotNull {
                rowToResult(it)
            }
        }
    }

    override suspend fun getResumeById(id: Long): Resume? {
        return DatabaseFactory.dbQuery {
            ResumeTable.select(ResumeTable.id.eq(id))
                .map { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteById(id: Long): Int {
        return DatabaseFactory.dbQuery {
            ResumeTable.deleteWhere { ResumeTable.id.eq(id) }
        }
    }

    override suspend fun updateResume(id: Long, categoryName: String, imageUrl: String): Int {
        return DatabaseFactory.dbQuery {
            ResumeTable.update({ ResumeTable.id.eq(id) }) { resume ->
                resume[ResumeTable.id] = id
                resume[ResumeTable.categoryName] = categoryName
                resume[ResumeTable.imageUrl] = imageUrl
            }
        }
    }

    private fun rowToResult(row: ResultRow): Resume? {
        return if (row == null) {
            null
        } else {
            Resume(
                id = row[ResumeTable.id],
                categoryName = row[ResumeTable.categoryName],
                imageUrl = row[ResumeTable.imageUrl]
            )
        }
    }
}