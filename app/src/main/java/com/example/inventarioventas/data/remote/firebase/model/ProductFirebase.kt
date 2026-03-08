package com.example.inventarioventas.data.remote.firebase.model

data class ProductFirebase(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val categoryId: Int = 0,
    val imageUrl: String? = null
)