package com.example.inventarioventas.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventarioventas.adapter.OnlineProductAdapter
import com.example.inventarioventas.data.remote.dto.ApiProductDto
import com.example.inventarioventas.databinding.ActivityOnlineCatalogBinding
import com.example.inventarioventas.data.repository.RepositoryProvider
import com.example.inventarioventas.domain.model.OnlineProduct
import com.example.inventarioventas.ui.viewmodel.OnlineCatalogViewModel
import com.example.inventarioventas.ui.viewmodel.factory.InventoryViewModelFactory
import kotlinx.coroutines.launch

// IMPORTANTE: Si la palabra "Result" sale en rojo más abajo,
// haz clic sobre ella y presiona Alt + Enter para importar tu clase Result.
import com.example.inventarioventas.utils.Result

class OnlineCatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnlineCatalogBinding
    private lateinit var onlineVM: OnlineCatalogViewModel
    private lateinit var adapter: OnlineProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configurar el diseño
        binding = ActivityOnlineCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Inicializar componentes
        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        // 3. ¡Llamar a la API al abrir la pantalla!
        onlineVM.cargarProductosOnline()
    }

    private fun setupViewModel() {
        val repo = RepositoryProvider.provide(applicationContext)
        val factory = InventoryViewModelFactory(repo)
        onlineVM = ViewModelProvider(this, factory)[OnlineCatalogViewModel::class.java]
    }

    private fun setupRecyclerView() {
        // Le decimos que es una lista de OnlineProduct
        adapter = OnlineProductAdapter(emptyList<OnlineProduct>()) { productoSeleccionado ->

            onlineVM.importar(productoSeleccionado)

            // Igual aquí: Si .title sale rojo, cámbialo a .name
            Toast.makeText(this, "${productoSeleccionado.title} importado a tu inventario", Toast.LENGTH_SHORT).show()
        }

        binding.rvOnlineCatalog.layoutManager = LinearLayoutManager(this)
        binding.rvOnlineCatalog.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            onlineVM.onlineProducts.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Mostrar la bolita de carga y ocultar la lista
                        binding.pbLoading.visibility = View.VISIBLE
                        binding.rvOnlineCatalog.visibility = View.GONE
                    }
                    is Result.Success -> {
                        // Ocultar carga, mostrar lista y pasarle los datos al Adapter
                        binding.pbLoading.visibility = View.GONE
                        binding.rvOnlineCatalog.visibility = View.VISIBLE
                        adapter.updateList(result.data)
                    }
                    is Result.Error -> {
                        // Ocultar carga y mostrar el error (ej: Sin internet)
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(this@OnlineCatalogActivity, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}