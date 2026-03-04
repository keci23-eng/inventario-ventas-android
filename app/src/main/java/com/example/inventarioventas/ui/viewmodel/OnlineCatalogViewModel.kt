package com.example.inventarioventas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioventas.data.repository.InventoryRepository

import com.example.inventarioventas.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnlineCatalogViewModel(
    private val repo: InventoryRepository
) : ViewModel() {

    private val _productsOnline = MutableStateFlow<Result<Any>>(Result.Loading)
    val productsOnline = _productsOnline.asStateFlow()

    fun cargarProductosOnline() = viewModelScope.launch {
        _productsOnline.value = Result.Loading
        try {
            val list = repo.obtenerProductosOnline() // debe devolver List<ApiProductDto> o similar
            _productsOnline.value = Result.Success(list as Any)
        } catch (e: Exception) {
            _productsOnline.value = Result.Error("No se pudo cargar el catálogo. Verifica tu conexión.")
        }
    }
}