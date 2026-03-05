package com.example.inventarioventas.repository

import com.example.inventarioventas.data.remote.api.CatalogApiService

import com.example.inventarioventas.domain.model.OnlineProduct
import com.example.inventarioventas.domain.model.mapper.toOnlineProduct
import com.example.inventarioventas.utils.Result

class OnlineCatalogRepositoryImpl(
    private val api: CatalogApiService
) : OnlineCatalogRepository {

    override suspend fun getOnlineProducts(): Result<List<OnlineProduct>> {
        return try {
            val list = api.getProducts().map { it.toOnlineProduct() }
            Result.Success(list)
        } catch (e: Exception) {
            Result.Error("No se pudo cargar el catálogo. Verifica tu conexión.")
        }
    }

    override suspend fun getOnlineCategories(): Result<List<String>> {
        return try {
            val list = api.getCategories()
            Result.Success(list)
        } catch (e: Exception) {
            Result.Error("No se pudieron cargar las categorías online.")
        }
    }
}