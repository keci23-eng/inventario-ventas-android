package com.example.inventarioventas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventarioventas.data.local.entity.Category
import com.example.inventarioventas.data.repository.InventoryRepository
import kotlinx.coroutines.launch


class CategoryViewModel(
    private val repo: InventoryRepository
) : ViewModel() {

    val categories = repo.getCategories().asLiveData()

    fun add(category: Category) = viewModelScope.launch {
        repo.addCategory(category)
    }

    fun update(category: Category) = viewModelScope.launch {
        repo.updateCategory(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repo.deleteCategory(category)
    }
}