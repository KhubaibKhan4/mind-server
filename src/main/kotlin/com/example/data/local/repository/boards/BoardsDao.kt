package com.example.data.local.repository.boards

import com.example.domain.model.papers.boards.Boards

interface BoardsDao {
    suspend fun insert(
        title: String,
        description: String,
        imageUrl: String
    ): Boards?

    suspend fun getAllBoards(): List<Boards>?
    suspend fun getBoardById(id: Long): Boards?
    suspend fun deleteBoardById(id: Long): Int
    suspend fun updateBoard(
        id: Long,
        title: String,
        description: String,
        imageUrl: String,
    ): Int
}