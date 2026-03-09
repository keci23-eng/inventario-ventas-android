package com.example.inventarioventas.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.inventarioventas.adapter.ProductAdapter
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.databinding.ActivityInventoryBinding
import com.example.inventarioventas.ui.viewmodel.ProductViewModel

class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var productVM: ProductViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del ViewModel
        val repo = com.example.inventarioventas.data.repository.RepositoryProvider.provide(applicationContext)
        val factory = com.example.inventarioventas.ui.viewmodel.factory.InventoryViewModelFactory(repo)
        productVM = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
        setupSwipeToDelete() // <-- 1. ACTIVAMOS EL DESLIZAMIENTO

        binding.fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddEditProduct::class.java)
            startActivity(intent)
        }

        binding.btnOpenCatalog.setOnClickListener {
            val intent = Intent(this, OnlineCatalogActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList<Product>()) { productoSeleccionado ->
            // 2. FUNCIÓN PARA MODIFICAR (CLICK)
            // Ahora que Product es Parcelable, esto ya no saldrá en rojo
            val intent = Intent(this, AddEditProduct::class.java)
            intent.putExtra("EXTRA_PRODUCTO_EDITAR", productoSeleccionado)
            startActivity(intent)
        }
        binding.rvInventory.layoutManager = LinearLayoutManager(this)
        binding.rvInventory.adapter = adapter
    }

    // 3. FUNCIÓN PARA BORRAR AL DESLIZAR
    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // No necesitamos mover de arriba a abajo
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Deslizar a izquierda o derecha
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Obtenemos la posición del elemento deslizado
                val position = viewHolder.adapterPosition
                val productoABorrar = adapter.getProductAt(position)

                // Llamamos al ViewModel para borrarlo de la base de datos
                productVM.delete(productoABorrar)

                Toast.makeText(this@InventoryActivity, "${productoABorrar.name} Producto Registrado en una venta", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvInventory)
    }

    private fun observeViewModel() {
        productVM.products.observe(this) { listaProductos ->
            adapter.updateList(listaProductos)
        }
    }
}