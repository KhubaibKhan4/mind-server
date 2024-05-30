package com.example.data.local.table.quiz

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object QuizSubCategoryTable: Table("quiz_sub_categories") {
    val id: Column<Long> = long("id").autoIncrement()
    val categoryName: Column<String> = varchar("categoryName",1000)
    val name: Column<String> = varchar("name",500)
    val description: Column<String> = varchar("description",500)
    val imageUrl: Column<String> = varchar("imageUrl",1000)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}