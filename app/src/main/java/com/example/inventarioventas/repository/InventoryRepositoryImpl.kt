package com.example.inventarioventas.data.repository

import com.example.inventarioventas.data.local.dao.*
import com.example.inventarioventas.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import com.example.inventarioventas.data.remote.api.CatalogApiService
import com.example.inventarioventas.utils.Result
import com.example.inventarioventas.data.remote.dto.ApiProductDto
import com.example.inventarioventas.domain.model.CreateSaleRequest
import com.example.inventarioventas.data.local.entity.Sale
import com.example.inventarioventas.data.local.entity.SaleItem
import java.util.Date

class InventoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao,
    private val catalogApiService: CatalogApiService,
    private val salesLocalTransaction: com.example.inventarioventas.data.local.transaction.SalesLocalTransaction
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
    override suspend fun obtenerProductosOnline(): Result<List<ApiProductDto>> {
        return try {
            val data = catalogApiService.getProducts()
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error("No se pudo cargar el catálogo. Verifica tu conexión.", e)
        }
    }

    override suspend fun obtenerCategoriasOnline(): Result<List<String>> {
        return try {
            val data = catalogApiService.getCategories()
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error("No se pudieron cargar las categorías. Verifica tu conexión.", e)
        }
    }

    override suspend fun obtenerProductosOnlinePorCategoria(categoria: String): Result<List<ApiProductDto>> {
        return try {
            val data = catalogApiService.getProductsByCategory(categoria)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error("No se pudo cargar la categoría seleccionada. Verifica tu conexión.", e)
        }
    }
    override suspend fun registrarVenta(request: CreateSaleRequest): Result<Long> {
        return try {
            if (request.items.isEmpty()) {
                return Result.Error("La venta no puede estar vacía.")
            }

            // 1) calcular total
            val total = request.items.sumOf { it.quantity * it.unitPrice }

            // 2) validar stock y preparar updates
            val stockUpdates = mutableListOf<Pair<Int, Int>>()

            request.items.forEach { item ->
                val product = productDao.getById(item.productId)
                    ?: return Result.Error("Producto no encontrado: ${item.productId}")

                if (item.quantity <= 0) return Result.Error("Cantidad inválida para ${product.name}")
                if (product.stock < item.quantity) return Result.Error("Stock insuficiente: ${product.name}")

                stockUpdates.add(product.id to (product.stock - item.quantity))
            }

            // 3) crear Sale
            val sale = Sale(
                customerId = request.customerId,
                date = Date(),
                total = total
            )

            // 4) crear items
            val items = request.items.map { i ->
                SaleItem(
                    saleId = 0, // se reemplaza dentro de la transacción
                    productId = i.productId,
                    quantity = i.quantity,
                    unitPrice = i.unitPrice
                )
            }

            // 5) transacción
            val saleId = salesLocalTransaction.createSaleWithItemsAndUpdateStock(
                sale = sale,
                items = items,
                stockUpdates = stockUpdates
            )

            Result.Success(saleId)
        } catch (e: Exception) {
            Result.Error("No se pudo registrar la venta.", e)
        }
    }
}