package com.example.data.local.repository.boards

import com.example.domain.model.papers.papers.Papers

interface PapersDao {
    fun insert(boardId: Long, classId: Long, subjectId: Long, pdfUrl: String): Papers?
    fun getPapersByBoardId(boardId: Long): List<Papers>?
    fun getPapersByClassId(classId: Long): List<Papers>?
    fun getPapersBySubjectId(subjectId: Long): List<Papers>?
}