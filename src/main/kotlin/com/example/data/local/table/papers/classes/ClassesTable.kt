package com.example.data.local.table.papers.classes

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ClassesTable : Table("classes") {
    val id: Column<Long> = long("id").autoIncrement()
    val boardId: Column<Long> = long("board_id")
    val title: Column<String> = varchar("name", 100)
    val description: Column<String> = varchar("description", 500)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
