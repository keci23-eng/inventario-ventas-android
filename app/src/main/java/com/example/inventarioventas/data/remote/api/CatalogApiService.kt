package com.example.inventarioventas.data.remote.api

import com.example.inventarioventas.data.remote.dto.ApiProductDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CatalogApiService {

    @GET("products")
    suspend fun getProducts(): List<ApiProductDto>

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): List<ApiProductDto>
}