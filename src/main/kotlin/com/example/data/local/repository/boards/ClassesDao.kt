package com.example.data.local.repository.boards

import com.example.domain.model.papers.classes.BoardClasses

interface ClassesDao {
    suspend fun insert(
        boardId: Long,
        title: String,
        description: String,
    ): BoardClasses?

    suspend fun getAllClasses(): List<BoardClasses>?
    suspend fun getClassesById(id: Long): BoardClasses?
    suspend fun getClassesByBoardId(id: Long): List<BoardClasses>?
    suspend fun deleteBoardById(id: Long): Int
    suspend fun updateBoard(
        id: Long,
        boardId: Long,
        title: String,
        description: String,
    ): Int
}