package com.example.data.local.repository.promotions

import com.example.domain.model.notes.Note
import com.example.domain.model.promotion.Promotions

interface PromotionDao {
    suspend fun insert(
        imageUrl: String
    ): Promotions?

    suspend fun getAllPromotions(): List<Promotions>?
    suspend fun getPromotionById(id: Long): Promotions?
    suspend fun deletePromotionById(id: Long): Int
    suspend fun updatePromotion(
        id: Long,
        imageUrl: String
    ): Int
}