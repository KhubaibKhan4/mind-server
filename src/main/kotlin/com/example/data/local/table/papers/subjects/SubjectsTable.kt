package com.example.data.local.table.papers.subjects

import org.jetbrains.exposed.sql.Table

object SubjectsTable : Table("subjects") {
    val id = long("id").autoIncrement()
    val boardId = long("board_id")
    val classId = long("class_id")
    val title = varchar("title", 255)
    val description = text("description")

    override val primaryKey: PrimaryKey? = PrimaryKey(id)
}
