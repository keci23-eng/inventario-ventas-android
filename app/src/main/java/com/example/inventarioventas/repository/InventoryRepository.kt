package com.example.inventarioventas.data.repository

import com.example.inventarioventas.data.local.entity.*
import com.example.inventarioventas.data.local.relation.SaleWithItems
import com.example.inventarioventas.data.remote.dto.ApiProductDto
import kotlinx.coroutines.flow.Flow
import com.example.inventarioventas.utils.Result
import com.example.inventarioventas.domain.model.CreateSaleRequest
import com.example.inventarioventas.domain.model.OnlineProduct

interface InventoryRepository {

    // -------------------------
    // CATEGORIES
    // -------------------------
    fun getCategories(): Flow<List<Category>>
    suspend fun inicializarCategoriasPorDefecto()
    suspend fun addCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)

    // -------------------------
    // PRODUCTS
    // -------------------------
    fun getProducts(): Flow<List<Product>>
    fun getProductsByCategory(categoryId: Int): Flow<List<Product>>
    fun searchProducts(query: String): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun addProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    suspend fun updateStock(productId: Int, newStock: Int)

    // -------------------------
    // CUSTOMERS
    // -------------------------
    fun getCustomers(): Flow<List<Customer>>
    fun searchCustomers(query: String): Flow<List<Customer>>
    suspend fun addCustomer(customer: Customer): Long
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)

    // -------------------------
    // SALES (TU CARRITO)
    // -------------------------
    fun getSales(): Flow<List<Sale>>
    fun getSalesByCustomer(customerId: Int): Flow<List<Sale>>
    suspend fun addSale(sale: Sale): Long
    fun getSaleItems(saleId: Int): Flow<List<SaleItem>>
    suspend fun addSaleItems(items: List<SaleItem>)
    suspend fun registrarVenta(request: CreateSaleRequest): Result<Long>
    fun getSalesHistory(): Flow<List<SaleWithItems>>

    // -------------------------
    // ONLINE CATALOG
    // -------------------------
    suspend fun obtenerProductosOnline(): Result<List<ApiProductDto>>
    suspend fun obtenerCategoriasOnline(): Result<List<String>>
    suspend fun obtenerProductosOnlinePorCategoria(categoria: String): Result<List<ApiProductDto>>
    suspend fun importarProductoDesdeOnline(p: OnlineProduct): Long
}