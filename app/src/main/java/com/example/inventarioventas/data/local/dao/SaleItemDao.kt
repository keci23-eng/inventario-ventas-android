package com.example.inventarioventas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inventarioventas.data.local.entity.SaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleItemDao {

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getItemsBySaleId(saleId: Int): Flow<List<SaleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SaleItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SaleItem>)
}