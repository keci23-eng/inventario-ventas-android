package com.example.inventarioventas.data.repository

import android.net.Uri
import com.example.inventarioventas.data.local.dao.*
import com.example.inventarioventas.data.local.entity.*
import com.example.inventarioventas.data.local.relation.SaleWithItems
import com.example.inventarioventas.data.local.transaction.SalesLocalTransaction
import com.example.inventarioventas.data.remote.api.CatalogApiService
import com.example.inventarioventas.data.remote.dto.ApiProductDto
import com.example.inventarioventas.data.remote.firebase.model.FirebaseFirestoreService
import com.example.inventarioventas.data.remote.firebase.model.FirebaseStorageService
import com.example.inventarioventas.domain.model.CreateSaleRequest
import com.example.inventarioventas.domain.model.OnlineProduct
import com.example.inventarioventas.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class InventoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao,
    private val catalogApiService: CatalogApiService,
    private val salesLocalTransaction: SalesLocalTransaction,
    // --- SERVICIOS DE FIREBASE DE TU COMPAÑERO ---
    private val firebaseFirestoreService: FirebaseFirestoreService,
    private val firebaseStorageService: FirebaseStorageService
) : InventoryRepository {

    // -------------------------
    // CATEGORIES
    // -------------------------
    override fun getCategories(): Flow<List<Category>> = categoryDao.getAll()

    override suspend fun addCategory(category: Category): Long {
        val id = categoryDao.insert(category)
        firebaseFirestoreService.uploadCategory(category.copy(id = id.toInt()))
        return id
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
        firebaseFirestoreService.uploadCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
        firebaseFirestoreService.deleteCategory(category.id)
    }

    override suspend fun inicializarCategoriasPorDefecto() {
        val categoriasActuales = categoryDao.getAll().first()

        if (categoriasActuales.isEmpty()) {
            val categoriasIniciales = listOf(
                Category(id = 0, name = "Ropa"),
                Category(id = 0, name = "Joyería"),
                Category(id = 0, name = "Zapatos"),
                Category(id = 0, name = "Accesorios"),
                Category(id = 0, name = "Tecnología"),
                Category(id = 0, name = "Hogar"),
                Category(id = 0, name = "Belleza"),
                Category(id = 0, name = "Papelería"),
                Category(id = 0, name = "Deportes"),
                Category(id = 0, name = "Alimentos")
            )

            categoriasIniciales.forEach { category ->
                val id = categoryDao.insert(category)
                firebaseFirestoreService.uploadCategory(category.copy(id = id.toInt()))
            }
        }
    }

    // -------------------------
    // PRODUCTS
    // -------------------------
    override fun getProducts(): Flow<List<Product>> = productDao.getAll()

    override fun getProductsByCategory(categoryId: Int): Flow<List<Product>> =
        productDao.getByCategory(categoryId)

    override fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchByName(query)

    override suspend fun getProductById(id: Int): Product? =
        productDao.getById(id)

    override suspend fun addProduct(product: Product): Long {
        val id = productDao.insert(product)
        val savedProduct = product.copy(id = id.toInt())

        val imageUrl = if (!savedProduct.imageUri.isNullOrEmpty()) {
            firebaseStorageService.uploadProductImage(
                productId = savedProduct.id,
                imageUri = Uri.parse(savedProduct.imageUri)
            )
        } else null

        firebaseFirestoreService.uploadProduct(savedProduct, imageUrl)
        return id
    }

    override suspend fun updateProduct(product: Product) {
        productDao.update(product)
        val imageUrl = if (!product.imageUri.isNullOrEmpty()) {
            firebaseStorageService.uploadProductImage(
                productId = product.id,
                imageUri = Uri.parse(product.imageUri)
            )
        } else null

        firebaseFirestoreService.uploadProduct(product, imageUrl)
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.delete(product)
        firebaseFirestoreService.deleteProduct(product.id)
    }

    override suspend fun updateStock(productId: Int, newStock: Int) {
        productDao.updateStock(productId, newStock)
        val product = productDao.getById(productId)
        if (product != null) {
            val imageUrl = if (!product.imageUri.isNullOrEmpty()) {
                firebaseStorageService.uploadProductImage(
                    productId = product.id,
                    imageUri = Uri.parse(product.imageUri)
                )
            } else null
            firebaseFirestoreService.uploadProduct(product, imageUrl)
        }
    }

    // -------------------------
    // CUSTOMERS
    // -------------------------
    override fun getCustomers(): Flow<List<Customer>> = customerDao.getAll()
    override fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchByName(query)

    override suspend fun addCustomer(customer: Customer): Long {
        val id = customerDao.insert(customer)
        firebaseFirestoreService.uploadCustomer(customer.copy(id = id.toInt()))
        return id
    }

    override suspend fun updateCustomer(customer: Customer) {
        customerDao.update(customer)
        firebaseFirestoreService.uploadCustomer(customer)
    }

    override suspend fun deleteCustomer(customer: Customer) {
        customerDao.delete(customer)
        firebaseFirestoreService.deleteCustomer(customer.id)
    }

    // -------------------------
    // SALES (TU LÓGICA CONSERVADA)
    // -------------------------
    override fun getSales(): Flow<List<Sale>> = saleDao.getAll()
    override fun getSalesByCustomer(customerId: Int): Flow<List<Sale>> = saleDao.getByCustomer(customerId)
    override fun getSaleItems(saleId: Int): Flow<List<SaleItem>> = saleItemDao.getItemsBySaleId(saleId)
    override fun getSalesHistory(): Flow<List<SaleWithItems>> = saleDao.getSalesWithItems()

    override suspend fun addSale(sale: Sale): Long {
        val id = saleDao.insert(sale)
        firebaseFirestoreService.uploadSale(sale.copy(id = id.toInt()))
        return id
    }

    override suspend fun addSaleItems(items: List<SaleItem>) {
        saleItemDao.insertAll(items)
        items.forEach { firebaseFirestoreService.uploadSaleItem(it) }
    }

    // -------------------------
    // REGISTRAR VENTA (TRANSACCIÓN LOCAL + FIREBASE)
    // -------------------------
    override suspend fun registrarVenta(request: CreateSaleRequest): Result<Long> {
        return try {
            // --- TU CÓDIGO INTACTO ---
            if (request.items.isEmpty()) {
                return Result.Error("La venta no puede estar vacía.")
            }

            val total = request.items.sumOf { it.quantity * it.unitPrice }
            val stockUpdates = mutableListOf<Pair<Int, Int>>()

            request.items.forEach { item ->
                val product = productDao.getById(item.productId)
                    ?: return Result.Error("Producto no encontrado: ${item.productId}")

                if (item.quantity <= 0) return Result.Error("Cantidad inválida para ${product.name}")
                if (product.stock < item.quantity) return Result.Error("Stock insuficiente: ${product.name}")

                stockUpdates.add(product.id to (product.stock - item.quantity))
            }

            val sale = Sale(
                customerId = request.customerId,
                date = System.currentTimeMillis(),
                total = total
            )

            val items = request.items.map { i ->
                SaleItem(
                    saleId = -1,
                    productId = i.productId,
                    quantity = i.quantity,
                    unitPrice = i.unitPrice
                )
            }

            // Transacción local principal
            val saleId = salesLocalTransaction.createSaleWithItemsAndUpdateStock(
                sale = sale,
                items = items,
                stockUpdates = stockUpdates
            )

            // --- CÓDIGO DE FIREBASE AÑADIDO (No interrumpe tu lógica si falla) ---
            try {
                val savedSale = Sale(
                    id = saleId.toInt(),
                    customerId = request.customerId,
                    date = sale.date,
                    total = total
                )
                firebaseFirestoreService.uploadSale(savedSale)

                items.map { it.copy(saleId = saleId.toInt()) }
                    .forEach { firebaseFirestoreService.uploadSaleItem(it) }

                stockUpdates.forEach { (productId, _) ->
                    val updatedProduct = productDao.getById(productId)
                    if (updatedProduct != null) {
                        val imageUrl = if (!updatedProduct.imageUri.isNullOrEmpty()) {
                            firebaseStorageService.uploadProductImage(
                                productId = updatedProduct.id,
                                imageUri = Uri.parse(updatedProduct.imageUri)
                            )
                        } else null
                        firebaseFirestoreService.uploadProduct(updatedProduct, imageUrl)
                    }
                }
            } catch (e: Exception) {
                // Si Firebase falla (ej. sin internet), la venta local ya está segura
            }

            Result.Success(saleId)
        } catch (e: Exception) {
            Result.Error("No se pudo registrar la venta.", e)
        }
    }

    // -------------------------
    // ONLINE CATALOG & IMPORT
    // -------------------------
    override suspend fun obtenerProductosOnline(): Result<List<ApiProductDto>> = try { Result.Success(catalogApiService.getProducts()) } catch (e: Exception) { Result.Error("Error", e) }
    override suspend fun obtenerCategoriasOnline(): Result<List<String>> = try { Result.Success(catalogApiService.getCategories()) } catch (e: Exception) { Result.Error("Error", e) }
    override suspend fun obtenerProductosOnlinePorCategoria(categoria: String): Result<List<ApiProductDto>> = try { Result.Success(catalogApiService.getProductsByCategory(categoria)) } catch (e: Exception) { Result.Error("Error", e) }

    override suspend fun importarProductoDesdeOnline(p: OnlineProduct): Long {
        val categoriasActuales = categoryDao.getAll().first()
        val categoriaExistente = categoriasActuales.find { it.name == p.category }

        val categoryId = categoriaExistente?.id ?: categoryDao.insert(Category(id = 0, name = p.category)).toInt()

        if (categoriaExistente == null) {
            firebaseFirestoreService.uploadCategory(Category(id = categoryId, name = p.category))
        }

        val product = Product(name = p.title, price = p.price, stock = 1, categoryId = categoryId, imageUri = null)
        val id = productDao.insert(product)
        firebaseFirestoreService.uploadProduct(product.copy(id = id.toInt()), null)

        return id
    }
}