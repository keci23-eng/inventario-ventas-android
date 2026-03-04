package com.example.inventarioventas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventarioventas.data.repository.InventoryRepository
import com.example.inventarioventas.domain.model.CreateSaleRequest

import com.example.inventarioventas.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesViewModel(
    private val repo: InventoryRepository
) : ViewModel() {

    val sales = repo.getSales().asLiveData()

    private val _saleStatus = MutableStateFlow<Result<Long>>(Result.Loading)
    val saleStatus = _saleStatus.asStateFlow()

    fun registrarVenta(request: CreateSaleRequest) = viewModelScope.launch {
        _saleStatus.value = Result.Loading
        val res = repo.registrarVenta(request)
        _saleStatus.value = res
    }
}