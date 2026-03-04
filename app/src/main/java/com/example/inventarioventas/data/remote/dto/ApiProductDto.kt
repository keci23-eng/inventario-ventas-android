package com.example.inventarioventas.data.remote.dto

data class ApiProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)