package com.example.data.local.repository.promotions

import com.example.domain.model.notes.Note

interface PromotionDao {
    suspend fun insert(
        imageUrl: String
    ): Note?

    suspend fun getAllPromotions(): List<Note>?
    suspend fun getPromotionById(id: Long): Note?
    suspend fun deletePromotionById(id: Long): Int
    suspend fun updatePromotion(
        id: Long,
        imageUrl: String
    ): Int
}