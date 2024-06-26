package com.example.data.local.db

import com.example.data.local.table.notes.NotesTable
import com.example.data.local.table.papers.boards.BoardsTable
import com.example.data.local.table.papers.classes.ClassesTable
import com.example.data.local.table.papers.papers.PapersTable
import com.example.data.local.table.papers.subjects.SubjectsTable
import com.example.data.local.table.promotions.PromotionsTable
import com.example.data.local.table.quiz.QuizCategoryTable
import com.example.data.local.table.quiz.QuizQuestionTable
import com.example.data.local.table.quiz.QuizQuestionTableWithSubCategory
import com.example.data.local.table.quiz.QuizSubCategoryTable
import com.example.data.local.table.resume.ResumeTable
import com.example.data.local.table.user.UserTable
import com.example.domain.repository.promotion.PromotionsRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(
                UserTable,
                QuizCategoryTable,
                QuizQuestionTable,
                NotesTable,
                BoardsTable,
                ClassesTable,
                SubjectsTable,
                PapersTable,
                QuizSubCategoryTable,
                QuizQuestionTableWithSubCategory,
                PromotionsTable,
                ResumeTable
            )
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER")
        config.jdbcUrl = System.getenv("JDBC_DATABASE_URL")
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction {
                block()
            }
        }
}