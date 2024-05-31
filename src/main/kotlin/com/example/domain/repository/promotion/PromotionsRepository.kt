package com.example.domain.repository.promotion

import com.example.data.local.db.DatabaseFactory
import com.example.data.local.repository.promotions.PromotionDao
import com.example.data.local.table.notes.NotesTable
import com.example.data.local.table.promotions.PromotionsTable
import com.example.domain.model.notes.Note
import com.example.domain.model.promotion.Promotions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class PromotionsRepository : PromotionDao {
    private fun rowToResult(row: ResultRow): Promotions? {
        if (row == null) {
            return null
        } else {
            return Promotions(
                id = row[PromotionsTable.id],
                imageUrl = row[PromotionsTable.imageUrl],
            )
        }
    }

    override suspend fun insert(imageUrl: String): Promotions? {
        var insertStatement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            insertStatement = PromotionsTable.insert { promotion ->
                promotion[PromotionsTable.imageUrl] = imageUrl
            }
        }
        return rowToResult(insertStatement?.resultedValues?.get(0)!!)
    }

    override suspend fun getAllPromotions(): List<Promotions>? {
        return DatabaseFactory.dbQuery {
            PromotionsTable.selectAll().mapNotNull { rowToResult(it) }
        }
    }

    override suspend fun getPromotionById(id: Long): Promotions? {
        return DatabaseFactory.dbQuery {
            PromotionsTable.select { PromotionsTable.id eq id }.map { rowToResult(it) }
                .singleOrNull()
        }
    }

    override suspend fun deletePromotionById(id: Long): Int {
       return DatabaseFactory.dbQuery {
           PromotionsTable.deleteWhere { PromotionsTable.id eq  id }
       }
    }

    override suspend fun updatePromotion(id: Long, imageUrl: String): Int {
        return DatabaseFactory.dbQuery {
            PromotionsTable.update({PromotionsTable.id.eq(id)}){promotion->
                promotion[PromotionsTable.id]= id
                promotion[PromotionsTable.imageUrl]=imageUrl
            }
        }
    }
}