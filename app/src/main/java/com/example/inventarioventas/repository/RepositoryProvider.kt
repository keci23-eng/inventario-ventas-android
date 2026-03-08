package com.example.inventarioventas.data.repository

import android.content.Context
import com.example.inventarioventas.data.local.db.AppDatabase
import com.example.inventarioventas.data.remote.api.RetrofitClient
import com.example.inventarioventas.data.local.transaction.SalesLocalTransaction
import com.example.inventarioventas.repository.OnlineCatalogRepositoryImpl
import com.example.inventarioventas.data.remote.firebase.model.FirebaseStorageService
import com.example.inventarioventas.data.remote.firebase.model.FirebaseFirestoreService
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
                saleItemDao = db.saleItemDao(),
                catalogApiService = RetrofitClient.catalogApi,
                salesLocalTransaction = SalesLocalTransaction(db),
                firebaseFirestoreService = FirebaseFirestoreService(),
                firebaseStorageService = FirebaseStorageService()
            )

            repository = repo
            repo
        }
    }
    val onlineRepo = OnlineCatalogRepositoryImpl(RetrofitClient.catalogApi)
}