package com.example.inventarioventas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioventas.data.local.entity.Customer
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.data.repository.InventoryRepository
import com.example.inventarioventas.domain.model.CartItem
import com.example.inventarioventas.domain.model.CreateSaleRequest
import com.example.inventarioventas.domain.model.CreateSaleItem
import com.example.inventarioventas.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditSaleViewModel(
    private val inventoryRepo: InventoryRepository
) : ViewModel() {

    // Estado para los productos disponibles
    private val _availableProducts = MutableStateFlow<List<Product>>(emptyList())
    val availableProducts: StateFlow<List<Product>> = _availableProducts.asStateFlow()

    // Estado para los clientes (Para el AutoCompleteTextView/Spinner)
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    // Estado del carrito de compras
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Estado del monto total
    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount.asStateFlow()

    // Inicialización: Carga automática de productos y clientes al instanciar el ViewModel
    init {
        // Carga de productos reales
        viewModelScope.launch {
            inventoryRepo.getProducts().collect { productsList ->
                _availableProducts.value = productsList
            }
        }

        // Carga de clientes reales para el selector
        viewModelScope.launch {
            inventoryRepo.getCustomers().collect { customerList ->
                _customers.value = customerList
            }
        }
    }

    // --- MÉTODOS DE GESTIÓN DEL CARRITO ---

    fun addToCart(product: Product) {
        val currentCart = _cart.value.toMutableList()
        val existingItemIndex = currentCart.indexOfFirst { it.productId == product.id }

        if (existingItemIndex != -1) {
            val item = currentCart[existingItemIndex]
            if (item.quantity < product.stock) {
                currentCart[existingItemIndex] = item.copy(quantity = item.quantity + 1)
            }
        } else {
            if (product.stock > 0) {
                val newItem = CartItem(
                    productId = product.id,
                    productName = product.name,
                    quantity = 1,
                    price = product.price
                )
                currentCart.add(newItem)
            }
        }
        updateCart(currentCart)
    }

    fun increaseQuantity(productId: Int, maxStockAvailable: Int) {
        val currentCart = _cart.value.toMutableList()
        val index = currentCart.indexOfFirst { it.productId == productId }

        if (index != -1) {
            val item = currentCart[index]
            if (item.quantity < maxStockAvailable) {
                currentCart[index] = item.copy(quantity = item.quantity + 1)
                updateCart(currentCart)
            }
        }
    }

    fun decreaseQuantity(productId: Int) {
        val currentCart = _cart.value.toMutableList()
        val index = currentCart.indexOfFirst { it.productId == productId }

        if (index != -1) {
            val item = currentCart[index]
            if (item.quantity > 1) {
                currentCart[index] = item.copy(quantity = item.quantity - 1)
            } else {
                currentCart.removeAt(index)
            }
            updateCart(currentCart)
        }
    }

    fun removeItem(productId: Int) {
        val currentCart = _cart.value.filter { it.productId != productId }
        updateCart(currentCart)
    }

    private fun updateCart(newCart: List<CartItem>) {
        _cart.value = newCart
        _totalAmount.value = newCart.sumOf { it.subtotal }
    }

    // --- OPERACIONES DE BASE DE DATOS ---

    /**
     * Confirma la venta procesando el carrito y el ID del cliente seleccionado
     */
    fun confirmSale(customerId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) {
            onError("El carrito está vacío")
            return
        }

        viewModelScope.launch {
            val request = CreateSaleRequest(
                customerId = customerId,
                items = currentCart.map { item ->
                    CreateSaleItem(
                        productId = item.productId,
                        quantity = item.quantity,
                        unitPrice = item.price
                    )
                }
            )

            // Registro de venta mediante transacción en el repositorio
            when (val result = inventoryRepo.registrarVenta(request)) {
                is Result.Success -> {
                    _cart.value = emptyList()
                    _totalAmount.value = 0.0
                    onSuccess()
                }
                is Result.Error -> {
                    onError(result.message)
                }
                else -> {
                    onError("Estado desconocido al registrar venta")
                }
            }
        }
    }

    /**
     * Registra un nuevo cliente en la base de datos local
     */
    fun saveCustomer(name: String, phone: String, email: String) {
        viewModelScope.launch {
            try {
                val newCustomer = Customer(
                    id = 0, // El ID 0 indica a Room que debe autogenerarlo
                    name = name,
                    phone = phone,
                    email = email
                )
                inventoryRepo.addCustomer(newCustomer)
            } catch (e: Exception) {
                // El manejo de errores puede extenderse con un StateFlow de UI si es necesario
            }
        }
    }
}