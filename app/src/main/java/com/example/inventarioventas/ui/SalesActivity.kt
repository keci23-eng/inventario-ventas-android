package com.example.inventarioventas.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioventas.data.repository.RepositoryProvider
import com.example.inventarioventas.databinding.ActivitySalesBinding
import com.example.inventarioventas.ui.adapter.SalesAdapter
import com.example.inventarioventas.ui.viewmodel.SalesViewModel
import com.example.inventarioventas.ui.viewmodel.factory.InventoryViewModelFactory

// import com.example.inventarioventas.ui.viewmodel.SalesViewModel // Descomenta esto cuando tengas el ViewModel

class SalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesBinding
    private lateinit var salesAdapter: SalesAdapter

    private lateinit var viewModel: SalesViewModel

    // Si usas una Factory para el ViewModel, agrégala aquí como en el Inventario
    // private val viewModel: SalesViewModel by viewModels { ... }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajuste de paddings para el diseño edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. CONFIGURACIÓN DEL VIEWMODEL (Igual que en tu InventoryActivity)
        val repo = RepositoryProvider.provide(applicationContext)
        val factory = InventoryViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[SalesViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        setupObservers() // 3. ¡DESCOMENTADO Y LISTO!
    }

    private fun setupObservers() {
        // 4. OBSERVAMOS LAS VENTAS: Aquí es donde ocurre la magia
        viewModel.sales.observe(this) { listaVentas ->
            // Cuando la base de datos cambia, actualizamos el adapter
            salesAdapter.updateList(listaVentas)
        }
    }

    private fun setupRecyclerView() {
        // 1. Configuramos el Adapter con la lista vacía inicial y la acción de CLIC
        salesAdapter = SalesAdapter(emptyList()) { ventaSeleccionada ->
            // Acción al tocar: Abrimos AddEditSaleActivity para editar
            val intent = Intent(this, AddEditSaleActivity::class.java).apply {
                putExtra("EXTRA_SALE_ID", ventaSeleccionada.id)
            }
            startActivity(intent)
        }

        binding.rvSales.apply {
            layoutManager = LinearLayoutManager(this@SalesActivity)
            adapter = salesAdapter
        }

        // 2. Lógica de Deslizar para Eliminar (Swipe)
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // val saleToDelete = salesAdapter.salesList[position] // Necesitarás hacer salesList pública o accesible

                // viewModel.deleteSale(saleToDelete) // Llamada al ViewModel para borrar de la DB

                Toast.makeText(this@SalesActivity, "Venta eliminada", Toast.LENGTH_SHORT).show()
                // Nota: El RecyclerView se actualizará solo si usas un Observer (Paso 3)
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvSales)
    }

    /* private fun setupObservers() {
        // 3. Observar cambios en la base de datos
        lifecycleScope.launch {
            viewModel.sales.collect { listaVentas ->
                salesAdapter.updateList(listaVentas)
            }
        }
    }
    */

    private fun setupListeners() {
        // 4. Botón flotante para Nueva Venta
        binding.fabAddSale.setOnClickListener {
            val intent = Intent(this, AddEditSaleActivity::class.java)
            startActivity(intent)
        }
    }
}