package com.example.domain.repository.quiz

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.quiz.QuizSubDao
import com.example.data.local.table.quiz.QuizSubCategoryTable
import com.example.domain.model.quiz.QuizCategory
import com.example.domain.model.quiz.QuizSubCategory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class QuizSubRepository : QuizSubDao {
    override suspend fun insert(
        categoryName: String,
        name: String,
        description: String,
        imageUrl: String
    ): QuizSubCategory? {

        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = QuizSubCategoryTable.insert {
                it[QuizSubCategoryTable.categoryName] = categoryName
                it[QuizSubCategoryTable.name] = name
                it[QuizSubCategoryTable.description] = description
                it[QuizSubCategoryTable.imageUrl] = imageUrl
            }
        }

        return rowToResult(insertStatement?.resultedValues?.get(0)!!)
    }

    override suspend fun getAllSubCategories(): List<QuizSubCategory>? {
        return DatabaseFactory.dbQuery {
            QuizSubCategoryTable.selectAll().mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getSubCategoryById(id: Long): QuizSubCategory? {
        return DatabaseFactory.dbQuery {
            QuizSubCategoryTable.select { QuizSubCategoryTable.id eq id }
                .mapNotNull { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteSubCategoryById(id: Long): Int {
        return DatabaseFactory.dbQuery {
            QuizSubCategoryTable.deleteWhere { QuizSubCategoryTable.id eq id }
        }
    }

    override suspend fun updateSubCategory(
        id: Long,
        categoryName: String,
        name: String,
        description: String,
        imageUrl: String
    ): Int {
        return DatabaseFactory.dbQuery {
            QuizSubCategoryTable.update({ QuizSubCategoryTable.id eq id }) {
                it[QuizSubCategoryTable.id]= id
                it[QuizSubCategoryTable.categoryName] = categoryName
                it[QuizSubCategoryTable.name] = name
                it[QuizSubCategoryTable.description] = description
                it[QuizSubCategoryTable.imageUrl] = imageUrl
            }
        }
    }

    private fun rowToResult(row: ResultRow): QuizSubCategory? {
        return row[QuizSubCategoryTable.id]?.let { id ->
            QuizSubCategory(
                id = id,
                categoryName =row[QuizSubCategoryTable.categoryName] ,
                name = row[QuizSubCategoryTable.name],
                description = row[QuizSubCategoryTable.description],
                imageUrl = row[QuizSubCategoryTable.imageUrl]
            )
        }
    }
}
