package com.example.inventarioventas.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inventarioventas.data.repository.InventoryRepository
import com.example.inventarioventas.ui.viewmodel.CategoryViewModel
import com.example.inventarioventas.ui.viewmodel.CustomerViewModel
import com.example.inventarioventas.ui.viewmodel.OnlineCatalogViewModel
import com.example.inventarioventas.ui.viewmodel.ProductViewModel
import com.example.inventarioventas.ui.viewmodel.SalesViewModel

class InventoryViewModelFactory(
    private val repo: InventoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductViewModel::class.java) ->
                ProductViewModel(repo) as T

            modelClass.isAssignableFrom(CategoryViewModel::class.java) ->
                CategoryViewModel(repo) as T

            modelClass.isAssignableFrom(CustomerViewModel::class.java) ->
                CustomerViewModel(repo) as T

            modelClass.isAssignableFrom(SalesViewModel::class.java) ->
                SalesViewModel(repo) as T

            modelClass.isAssignableFrom(OnlineCatalogViewModel::class.java) ->
                OnlineCatalogViewModel(repo) as T

            else -> throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
        }
    }
}