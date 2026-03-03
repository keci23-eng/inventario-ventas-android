package com.example.inventarioventas.data.repository

import com.example.inventarioventas.data.local.dao.*
import com.example.inventarioventas.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class InventoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao
) : InventoryRepository {

    // Categories
    override fun getCategories(): Flow<List<Category>> = categoryDao.getAll()
    override suspend fun addCategory(category: Category): Long = categoryDao.insert(category)
    override suspend fun updateCategory(category: Category) = categoryDao.update(category)
    override suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

    // Products
    override fun getProducts(): Flow<List<Product>> = productDao.getAll()
    override fun getProductsByCategory(categoryId: Int): Flow<List<Product>> = productDao.getByCategory(categoryId)
    override fun searchProducts(query: String): Flow<List<Product>> = productDao.searchByName(query)
    override suspend fun getProductById(id: Int): Product? = productDao.getById(id)
    override suspend fun addProduct(product: Product): Long = productDao.insert(product)
    override suspend fun updateProduct(product: Product) = productDao.update(product)
    override suspend fun deleteProduct(product: Product) = productDao.delete(product)
    override suspend fun updateStock(productId: Int, newStock: Int) = productDao.updateStock(productId, newStock)

    // Customers
    override fun getCustomers(): Flow<List<Customer>> = customerDao.getAll()
    override fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchByName(query)
    override suspend fun addCustomer(customer: Customer): Long = customerDao.insert(customer)
    override suspend fun updateCustomer(customer: Customer) = customerDao.update(customer)
    override suspend fun deleteCustomer(customer: Customer) = customerDao.delete(customer)

    // Sales
    override fun getSales(): Flow<List<Sale>> = saleDao.getAll()
    override fun getSalesByCustomer(customerId: Int): Flow<List<Sale>> = saleDao.getByCustomer(customerId)
    override suspend fun addSale(sale: Sale): Long = saleDao.insert(sale)

    override fun getSaleItems(saleId: Int): Flow<List<SaleItem>> = saleItemDao.getItemsBySaleId(saleId)
    override suspend fun addSaleItems(items: List<SaleItem>) = saleItemDao.insertAll(items)
}