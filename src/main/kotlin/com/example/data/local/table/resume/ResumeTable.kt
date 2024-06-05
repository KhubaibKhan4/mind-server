package com.example.data.local.table.resume

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ResumeTable:Table("resume") {
    val id: Column<Long> = long("id").autoIncrement()
    val categoryName: Column<String> = varchar("categoryName",500)
    val imageUrl: Column<String> = varchar("imageUrl",750)
    override val primaryKey: PrimaryKey?= PrimaryKey(id)
}