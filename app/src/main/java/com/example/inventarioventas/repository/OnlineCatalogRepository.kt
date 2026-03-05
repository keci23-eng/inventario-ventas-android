package com.example.inventarioventas.repository

import com.example.inventarioventas.domain.model.OnlineProduct
import com.example.inventarioventas.utils.Result

interface OnlineCatalogRepository {
    suspend fun getOnlineProducts(): Result<List<OnlineProduct>>
    suspend fun getOnlineCategories(): Result<List<String>>
}