package com.example.inventarioventas.data.repository

import android.content.Context
import com.example.inventarioventas.data.local.db.AppDatabase

object RepositoryProvider {

    @Volatile private var repository: InventoryRepository? = null

    fun provide(context: Context): InventoryRepository {
        return repository ?: synchronized(this) {
            val db = AppDatabase.getInstance(context.applicationContext)
            val repo = InventoryRepositoryImpl(
                categoryDao = db.categoryDao(),
                productDao = db.productDao(),
                customerDao = db.customerDao(),
                saleDao = db.saleDao(),
                saleItemDao = db.saleItemDao()
            )
            repository = repo
            repo
        }
    }
}