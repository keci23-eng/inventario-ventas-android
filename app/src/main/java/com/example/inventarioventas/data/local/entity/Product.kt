package com.example.inventarioventas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val price: Double,
    val stock: Int,
    val categoryId: Int
)