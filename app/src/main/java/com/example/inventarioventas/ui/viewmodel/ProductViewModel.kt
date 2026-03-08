package com.example.inventarioventas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class ProductViewModel(
    private val inventoryRepo: InventoryRepository
) : ViewModel() {

    // Usamos inventoryRepo que es el nombre del parámetro arriba
    val products = inventoryRepo.getProducts().asLiveData()

    fun add(product: Product) = viewModelScope.launch {
        inventoryRepo.addProduct(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        inventoryRepo.updateProduct(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        inventoryRepo.deleteProduct(product)
    }

    fun updateStock(productId: Int, newStock: Int) = viewModelScope.launch {
        inventoryRepo.updateStock(productId, newStock)
    }
}