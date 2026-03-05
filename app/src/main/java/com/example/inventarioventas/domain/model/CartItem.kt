package com.example.inventarioventas.domain.model

data class CartItem(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double
)