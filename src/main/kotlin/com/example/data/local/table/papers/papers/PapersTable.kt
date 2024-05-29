package com.example.data.local.table.papers.papers

import org.jetbrains.exposed.dao.id.LongIdTable

object PapersTable : LongIdTable("papers") {
    val boardId = long("board_id")
    val classId = long("class_id")
    val subjectId = long("subject_id")
    val pdfUrl = varchar("pdf_url", 255)
}