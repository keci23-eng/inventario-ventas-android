package com.example.inventarioventas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.inventarioventas.data.local.entity.Sale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAll(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Sale?

    @Query("SELECT * FROM sales WHERE customerId = :customerId ORDER BY date DESC")
    fun getByCustomer(customerId: Int): Flow<List<Sale>>

    @Insert
    suspend fun insert(sale: Sale): Long
}