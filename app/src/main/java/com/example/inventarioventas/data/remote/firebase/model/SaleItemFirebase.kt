package com.example.inventarioventas.data.remote.firebase.model

data class SaleItemFirebase(
    val saleId: Int = 0,
    val productId: Int = 0,
    val quantity: Int = 0,
    val unitPrice: Double = 0.0
)