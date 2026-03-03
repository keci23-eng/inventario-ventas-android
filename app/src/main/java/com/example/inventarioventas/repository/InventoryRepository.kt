package com.example.inventarioventas.data.repository

import com.example.inventarioventas.data.local.entity.*
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {

    // Categories
    fun getCategories(): Flow<List<Category>>
    suspend fun addCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)

    // Products
    fun getProducts(): Flow<List<Product>>
    fun getProductsByCategory(categoryId: Int): Flow<List<Product>>
    fun searchProducts(query: String): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun addProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    suspend fun updateStock(productId: Int, newStock: Int)

    // Customers
    fun getCustomers(): Flow<List<Customer>>
    fun searchCustomers(query: String): Flow<List<Customer>>
    suspend fun addCustomer(customer: Customer): Long
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)

    // Sales (simple por ahora)
    fun getSales(): Flow<List<Sale>>
    fun getSalesByCustomer(customerId: Int): Flow<List<Sale>>
    suspend fun addSale(sale: Sale): Long

    fun getSaleItems(saleId: Int): Flow<List<SaleItem>>
    suspend fun addSaleItems(items: List<SaleItem>)
}