package com.example.data.local.table.notes

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object NotesTable:Table("notes") {
    val id: Column<Long> = long("id").autoIncrement()
    val title: Column<String> = varchar("title",500)
    val description: Column<String> = varchar("description",750)
    val pdfUrl: Column<String> = varchar("pdfUrl",750)
    override val primaryKey: PrimaryKey?= PrimaryKey(id)
}