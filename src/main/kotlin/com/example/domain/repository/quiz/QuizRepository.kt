package com.example.domain.repository.quiz

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.quiz.QuizDao
import com.example.data.local.table.quiz.QuizCategoryTable
import com.example.domain.model.quiz.QuizCategory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class QuizRepository : QuizDao {
    override suspend fun insert(name: String, description: String, imageUrl: String): QuizCategory? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = QuizCategoryTable.insert { quiz ->
                quiz[QuizCategoryTable.name] = name
                quiz[QuizCategoryTable.description] = description
                quiz[QuizCategoryTable.imageUrl] = imageUrl
            }
        }
        return rowToResult(statement?.resultedValues?.get(0)!!)
    }

    override suspend fun getAllCategories(): List<QuizCategory>? {
        return DatabaseFactory.dbQuery {
            QuizCategoryTable.selectAll().mapNotNull {
                rowToResult(it)
            }
        }

    }

    override suspend fun getCategoryById(id: Long): QuizCategory? {
        return DatabaseFactory.dbQuery {
            QuizCategoryTable.select {
                QuizCategoryTable.id.eq(id)
            }.map {
                rowToResult(it)
            }.singleOrNull()
        }
    }

    override suspend fun deleteCategoryById(id: Long): Int {
        return DatabaseFactory.dbQuery {
            QuizCategoryTable.deleteWhere {
                QuizCategoryTable.id.eq(id)
            }
        }
    }

    override suspend fun updateCategory(id: Long, name: String, description: String, imageUrl: String): Int {
        return DatabaseFactory.dbQuery {
            QuizCategoryTable.update({ QuizCategoryTable.id.eq(id) }) { quiz ->
                quiz[QuizCategoryTable.id] = id
                quiz[QuizCategoryTable.name] = name
                quiz[QuizCategoryTable.description] = description
                quiz[QuizCategoryTable.imageUrl] = imageUrl
            }
        }
    }

    private fun rowToResult(row: ResultRow): QuizCategory? {
        if (row == null) {
            return null
        } else {
            return QuizCategory(
                id = row[QuizCategoryTable.id],
                name = row[QuizCategoryTable.name],
                description = row[QuizCategoryTable.description],
                imageUrl = row[QuizCategoryTable.imageUrl]
            )
        }
    }
}