package com.example.inventarioventas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "sale_items",
    primaryKeys = ["saleId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("saleId"), Index("productId")]
)
data class SaleItem(
    val saleId: Int,
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double
)