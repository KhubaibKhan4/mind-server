package com.example.data.local.table.promotions

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PromotionsTable: Table("promotions") {
    val id: Column<Long> = long("id")
    val imageUrl: Column<String> = varchar("imageUrl",500)
}