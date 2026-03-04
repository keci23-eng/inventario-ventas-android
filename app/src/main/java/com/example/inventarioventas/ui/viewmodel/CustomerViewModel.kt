package com.example.inventarioventas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventarioventas.data.local.entity.Customer
import com.example.inventarioventas.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val repo: InventoryRepository
) : ViewModel() {

    val customers = repo.getCustomers().asLiveData()

    fun search(query: String) = repo.searchCustomers(query).asLiveData()

    fun add(customer: Customer) = viewModelScope.launch {
        repo.addCustomer(customer)
    }

    fun update(customer: Customer) = viewModelScope.launch {
        repo.updateCustomer(customer)
    }

    fun delete(customer: Customer) = viewModelScope.launch {
        repo.deleteCustomer(customer)
    }
}