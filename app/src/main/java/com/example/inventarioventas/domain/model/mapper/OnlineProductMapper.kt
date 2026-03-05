package com.example.inventarioventas.domain.model.mapper

import com.example.inventarioventas.data.remote.dto.ApiProductDto
import com.example.inventarioventas.domain.model.OnlineProduct

fun ApiProductDto.toOnlineProduct(): OnlineProduct {
    return OnlineProduct(
        id = id,
        title = title,
        price = price,
        category = category,
        imageUrl = image
    )
}