package com.example.inventarioventas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioventas.data.repository.InventoryRepository
import com.example.inventarioventas.domain.model.OnlineProduct
import com.example.inventarioventas.repository.OnlineCatalogRepository
import com.example.inventarioventas.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnlineCatalogViewModel(
    private val onlineRepo: OnlineCatalogRepository,
    private val inventoryRepo: InventoryRepository
) : ViewModel() {

    private val _onlineProducts = MutableStateFlow<Result<List<OnlineProduct>>>(Result.Loading)
    val onlineProducts = _onlineProducts.asStateFlow()

    private val _importStatus = MutableStateFlow<Result<Long>?>(null)
    val importStatus = _importStatus.asStateFlow()

    fun cargarProductosOnline() = viewModelScope.launch {
        _onlineProducts.value = Result.Loading
        _onlineProducts.value = onlineRepo.getOnlineProducts()
    }

    fun importar(product: OnlineProduct) = viewModelScope.launch {
        _importStatus.value = Result.Loading
        try {
            val id = inventoryRepo.importarProductoDesdeOnline(product)
            _importStatus.value = Result.Success(id)
        } catch (e: Exception) {
            _importStatus.value = Result.Error("No se pudo importar el producto.")
        }
    }
}