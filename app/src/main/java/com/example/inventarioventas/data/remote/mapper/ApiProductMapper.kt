package com.example.inventarioventas.data.remote.mapper

import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.data.remote.dto.ApiProductDto

fun ApiProductDto.toLocalProduct(categoryId: Int): Product {
    return Product(
        name = title,
        price = price,
        stock = 0,
        categoryId = categoryId
    )
}