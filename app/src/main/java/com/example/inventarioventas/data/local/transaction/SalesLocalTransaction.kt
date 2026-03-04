package com.example.inventarioventas.data.local.transaction

import androidx.room.withTransaction
import com.example.inventarioventas.data.local.db.AppDatabase
import com.example.inventarioventas.data.local.entity.Sale
import com.example.inventarioventas.data.local.entity.SaleItem

class SalesLocalTransaction(
    private val db: AppDatabase
) {
    suspend fun createSaleWithItemsAndUpdateStock(
        sale: Sale,
        items: List<SaleItem>,
        stockUpdates: List<Pair<Int, Int>> // productId -> newStock
    ): Long {
        return db.withTransaction {
            val saleId = db.saleDao().insert(sale)

            val itemsWithSaleId = items.map { it.copy(saleId = saleId.toInt()) }
            db.saleItemDao().insertAll(itemsWithSaleId)

            stockUpdates.forEach { (productId, newStock) ->
                db.productDao().updateStock(productId, newStock)
            }

            saleId
        }
    }
}