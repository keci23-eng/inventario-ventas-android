package com.example.inventarioventas.domain.model

data class OnlineProduct(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val imageUrl: String
)