package com.example.data.local.table.papers.boards

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object BoardsTable: Table("boards") {
    val id: Column<Long> = long("id").autoIncrement()
    val title: Column<String> = varchar("name",500)
    val description: Column<String> = varchar("description",500)
    val imageUrl: Column<String> = varchar("imageUrl",1000)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}