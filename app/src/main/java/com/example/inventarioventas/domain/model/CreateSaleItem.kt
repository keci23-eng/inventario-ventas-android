package com.example.inventarioventas.domain.model

data class CreateSaleItem(
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double
)