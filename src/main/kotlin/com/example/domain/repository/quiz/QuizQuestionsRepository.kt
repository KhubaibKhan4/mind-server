package com.example.domain.repository.quiz

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.quiz.QuizQuestionDao
import com.example.data.local.table.quiz.QuizQuestionTable
import com.example.domain.model.quiz.QuizQuestion
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class QuizQuestionsRepository : QuizQuestionDao {

    private fun rowToResult(row: ResultRow): QuizQuestion {
        return QuizQuestion(
            id = row[QuizQuestionTable.id],
            categoryId = row[QuizQuestionTable.categoryId],
            categoryTitle = row[QuizQuestionTable.categoryTitle],
            title = row[QuizQuestionTable.title],
            answer1 = row[QuizQuestionTable.answer1],
            answer2 = row[QuizQuestionTable.answer2],
            answer3 = row[QuizQuestionTable.answer3],
            answer4 = row[QuizQuestionTable.answer4],
            correctAnswer = row[QuizQuestionTable.correctAnswer]
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
    ): QuizQuestion? {
        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = QuizQuestionTable.insert { quiz ->
                quiz[QuizQuestionTable.categoryId] = categoryId
                quiz[QuizQuestionTable.categoryTitle] = categoryTitle
                quiz[QuizQuestionTable.title] = title
                quiz[QuizQuestionTable.answer1] = answer1
                quiz[QuizQuestionTable.answer2] = answer2
                quiz[QuizQuestionTable.answer3] = answer3
                quiz[QuizQuestionTable.answer4] = answer4
                quiz[QuizQuestionTable.correctAnswer] = correctAnswer
            }
        }
        return rowToResult(insertStatement?.resultedValues?.get(0)!!)
    }

    override suspend fun getAllQuestions(): List<QuizQuestion>? {
        return DatabaseFactory.dbQuery {
            QuizQuestionTable.selectAll().mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getQuizByCategoryId(categoryId: Long): List<QuizQuestion>? {
        return DatabaseFactory.dbQuery {
            QuizQuestionTable.select { QuizQuestionTable.categoryId eq categoryId }
                .map { rowToResult(it) }
        }
    }

    override suspend fun getQuizById(Id: Long): QuizQuestion? {
        return DatabaseFactory.dbQuery {
            QuizQuestionTable.select { QuizQuestionTable.id eq Id }.map { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun deleteQuizById(id: Long): Int {
        return DatabaseFactory.dbQuery {
            QuizQuestionTable.deleteWhere { QuizQuestionTable.id eq id }
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
            QuizQuestionTable.update({ QuizQuestionTable.id eq id }) {
                it[QuizQuestionTable.categoryId] = categoryId
                it[QuizQuestionTable.categoryTitle] = categoryTitle
                it[QuizQuestionTable.title] = title
                it[QuizQuestionTable.answer1] = answer1
                it[QuizQuestionTable.answer2] = answer2
                it[QuizQuestionTable.answer3] = answer3
                it[QuizQuestionTable.answer4] = answer4
                it[QuizQuestionTable.correctAnswer] = correctAnswer
            }
        }
    }
}