package com.example.inventarioventas.domain.model

data class CreateSaleRequest(
    val customerId: Int,
    val items: List<CreateSaleItem>
)