package com.example.domain.repository.quiz

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.quiz.QuizQuestionDao
import com.example.data.local.repository.quiz.QuizQuestionDaoWithSubCategory
import com.example.data.local.table.quiz.QuizQuestionTable
import com.example.data.local.table.quiz.QuizQuestionTableWithSubCategory
import com.example.domain.model.quiz.QuizQuestion
import com.example.domain.model.quiz.QuizQuestionWithSubCategory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class QuizQuestionsRepositoryWithSubCategory : QuizQuestionDaoWithSubCategory {

    private fun rowToResult(row: ResultRow): QuizQuestionWithSubCategory {
        return QuizQuestionWithSubCategory(
            id = row[QuizQuestionTableWithSubCategory.id],
            categoryId = row[QuizQuestionTableWithSubCategory.categoryId],
            categoryTitle = row[QuizQuestionTableWithSubCategory.categoryTitle],
            title = row[QuizQuestionTableWithSubCategory.title],
            answer1 = row[QuizQuestionTableWithSubCategory.answer1],
            answer2 = row[QuizQuestionTableWithSubCategory.answer2],
            answer3 = row[QuizQuestionTableWithSubCategory.answer3],
            answer4 = row[QuizQuestionTableWithSubCategory.answer4],
            correctAnswer = row[QuizQuestionTableWithSubCategory.correctAnswer]
        )
    }

    override suspend fun insert(
        categoryId: Long,
        categoryTitle: String,
        title: String,
        answer1: String,
        answer2: String,
        answer3: String,
        answer4: String,
        correctAnswer: String
    ): QuizQuestionWithSubCategory? {
        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = QuizQuestionTableWithSubCategory.insert { quiz ->
                quiz[QuizQuestionTableWithSubCategory.categoryId] = categoryId
                quiz[QuizQuestionTableWithSubCategory.categoryTitle] = categoryTitle
                quiz[QuizQuestionTableWithSubCategory.title] = title
                quiz[QuizQuestionTableWithSubCategory.answer1] = answer1
                quiz[QuizQuestionTableWithSubCategory.answer2] = answer2
                quiz[QuizQuestionTableWithSubCategory.answer3] = answer3
                quiz[QuizQuestionTableWithSubCategory.answer4] = answer4
                quiz[QuizQuestionTableWithSubCategory.correctAnswer] = correctAnswer
            }
        }
        return insertStatement?.resultedValues?.get(0)?.let { rowToResult(it) }
    }

    override suspend fun getAllQuestions(): List<QuizQuestionWithSubCategory>? {
        return DatabaseFactory.dbQuery {
            QuizQuestionTableWithSubCategory.selectAll().mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getQuizByCategoryId(categoryId: Long): List<QuizQuestionWithSubCategory>? {
        return DatabaseFactory.dbQuery {
            QuizQuestionTableWithSubCategory.select { QuizQuestionTableWithSubCategory.categoryId eq categoryId }
                .mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getQuizById(Id: Long): QuizQuestionWithSubCategory? {
        return DatabaseFactory.dbQuery {
            QuizQuestionTableWithSubCategory.select { QuizQuestionTableWithSubCategory.id eq Id }
                .mapNotNull { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteQuizById(id: Long): Int {
        return DatabaseFactory.dbQuery {
            QuizQuestionTableWithSubCategory.deleteWhere { QuizQuestionTableWithSubCategory.id eq id }
        }
    }

    override suspend fun updateQuiz(
        id: Long,
        categoryId: Long,
        categoryTitle: String,
        title: String,
        answer1: String,
        answer2: String,
        answer3: String,
        answer4: String,
        correctAnswer: String
    ): Int {
        return DatabaseFactory.dbQuery {
            QuizQuestionTableWithSubCategory.update({ QuizQuestionTableWithSubCategory.id eq id }) {
                it[QuizQuestionTableWithSubCategory.categoryId] = categoryId
                it[QuizQuestionTableWithSubCategory.categoryTitle] = categoryTitle
                it[QuizQuestionTableWithSubCategory.title] = title
                it[QuizQuestionTableWithSubCategory.answer1] = answer1
                it[QuizQuestionTableWithSubCategory.answer2] = answer2
                it[QuizQuestionTableWithSubCategory.answer3] = answer3
                it[QuizQuestionTableWithSubCategory.answer4] = answer4
                it[QuizQuestionTableWithSubCategory.correctAnswer] = correctAnswer
            }
        }
    }
}