package com.example.inventarioventas.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.databinding.ActivityAddEditProductBinding
import com.example.inventarioventas.ui.viewmodel.ProductViewModel
import com.example.inventarioventas.ui.viewmodel.factory.InventoryViewModelFactory
import com.example.inventarioventas.data.repository.RepositoryProvider

class AddEditProduct : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditProductBinding
    private lateinit var productVM: ProductViewModel

    // 1. Variable para saber si estamos editando
    private var productoAEditar: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        // 2. Intentar recibir el producto desde InventoryActivity
        productoAEditar = intent.getParcelableExtra("EXTRA_PRODUCTO_EDITAR")

        // 3. Si el producto existe, llenar los campos para editar
        if (productoAEditar != null) {
            llenarDatos(productoAEditar!!)
            binding.tvTitle.text = "Editar Producto"
            binding.btnSave.text = "Actualizar Cambios"
        }

        binding.btnSave.setOnClickListener {
            guardarProducto()
        }
    }

    private fun llenarDatos(p: Product) {
        binding.etName.setText(p.name)
        binding.etPrice.setText(p.price.toString())
        binding.etStock.setText(p.stock.toString())
    }

    private fun setupViewModel() {
        val repo = RepositoryProvider.provide(applicationContext)
        val factory = InventoryViewModelFactory(repo)
        productVM = ViewModelProvider(this, factory)[ProductViewModel::class.java]
    }

    private fun guardarProducto() {
        val name = binding.etName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val stockStr = binding.etStock.text.toString().trim()

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val price = priceStr.toDouble()
            val stock = stockStr.toInt()

            if (productoAEditar == null) {
                // MODO AGREGAR: El ID es 0 para que Room lo autogenere
                val nuevo = Product(id = 0, name = name, price = price, stock = stock, categoryId = 1)
                productVM.add(nuevo)
                Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
            } else {
                // MODO EDITAR: Mantenemos el mismo ID original para que Room lo actualice
                val actualizado = Product(
                    id = productoAEditar!!.id,
                    name = name,
                    price = price,
                    stock = stock,
                    categoryId = productoAEditar!!.categoryId
                )
                productVM.update(actualizado)
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
            }

            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error en el formato de datos", Toast.LENGTH_SHORT).show()
        }
    }
}