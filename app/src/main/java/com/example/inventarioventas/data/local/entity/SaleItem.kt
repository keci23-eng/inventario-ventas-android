package com.example.inventarioventas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "sale_items",
    primaryKeys = ["saleId", "productId"],
    foreignKeys = [
        // Si borro la venta, SÍ quiero borrar sus detalles (CASCADE)
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        ),
        // Si intento borrar un producto que ya se vendió, BLOQUÉALO (RESTRICT) para no dañar el historial
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("saleId"), Index("productId")]
)
data class SaleItem(
    val saleId: Int,
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double // Al igual que en Sale, Double está bien, pero recuerda el tema de los centavos a futuro
)