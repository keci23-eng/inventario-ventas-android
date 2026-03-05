package com.example.inventarioventas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.data.repository.InventoryRepository

import kotlinx.coroutines.launch

class ProductViewModel(
    private val repo: InventoryRepository
) : ViewModel() {

    val products = repo.getProducts().asLiveData()

    fun add(product: Product) = viewModelScope.launch {
        repo.addProduct(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        repo.updateProduct(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        repo.deleteProduct(product)
    }

    fun updateStock(productId: Int, newStock: Int) = viewModelScope.launch {
        repo.updateStock(productId, newStock)
    }

}