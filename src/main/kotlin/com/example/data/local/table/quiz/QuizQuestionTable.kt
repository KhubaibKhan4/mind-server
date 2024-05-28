package com.example.data.local.table.quiz

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object QuizQuestionTable : Table("quiz_questions") {
    val id: Column<Long> = long("id").autoIncrement()
    val title: Column<String> = varchar("title", 750)
    val answer1: Column<String> = varchar("answer1", 500)
    val answer2: Column<String> = varchar("answer2", 500)
    val answer3: Column<String> = varchar("answer3", 500)
    val answer4: Column<String> = varchar("answer4", 500)
    val correctAnswer: Column<String> = varchar("correctAnswer", 500)

    override val primaryKey: PrimaryKey? = PrimaryKey(id)
}