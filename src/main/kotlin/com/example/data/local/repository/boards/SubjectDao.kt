package com.example.data.local.repository.boards

import com.example.domain.model.papers.subjects.Subjects

interface SubjectsDao {
    suspend fun insert(boardId: Long, classId: Long, title: String, description: String): Subjects?
    suspend fun getAllSubjects(): List<Subjects>?
    suspend fun getSubjectById(id: Long): Subjects?
    suspend fun getSubjectsByBoardId(boardId: Long): List<Subjects>?
    suspend fun getSubjectsByClassId(classId: Long): List<Subjects>?
    suspend fun deleteSubjectById(id: Long): Int
    suspend fun updateSubject(id: Long, boardId: Long, classId: Long, title: String, description: String): Int
}
