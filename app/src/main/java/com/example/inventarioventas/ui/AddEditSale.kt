package com.example.inventarioventas.ui

import android.os.Bundle
import android.widget.ArrayAdapter // Importación necesaria para el selector
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.inventarioventas.data.local.entity.Customer
import com.example.inventarioventas.data.repository.RepositoryProvider
import com.example.inventarioventas.databinding.ActivityAddEditSaleBinding
import com.example.inventarioventas.databinding.DialogAddCustomerBinding
import com.example.inventarioventas.ui.adapter.CartAdapter
import com.example.inventarioventas.ui.adapter.ProductCartAdapter
import com.example.inventarioventas.ui.viewmodel.AddEditSaleViewModel
import com.example.inventarioventas.ui.viewmodel.factory.InventoryViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddEditSaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditSaleBinding
    private lateinit var viewModel: AddEditSaleViewModel
    private lateinit var productsAdapter: ProductCartAdapter
    private lateinit var cartAdapter: CartAdapter

    // Variable para rastrear al cliente seleccionado mediante su ID real
    private var selectedCustomerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerViews()
        setupObservers()
        setupCustomerSelector() // Llamada al nuevo selector dinámico
        setupListeners()
    }

    private fun setupViewModel() {
        val repo = RepositoryProvider.provide(applicationContext)
        val factory = InventoryViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[AddEditSaleViewModel::class.java]
    }

    private fun setupRecyclerViews() {
        productsAdapter = ProductCartAdapter { productoSeleccionado ->
            viewModel.addToCart(productoSeleccionado)
        }
        binding.rvProducts.adapter = productsAdapter

        cartAdapter = CartAdapter(
            onIncrease = { productId -> viewModel.increaseQuantity(productId, 999) },
            onDecrease = { productId -> viewModel.decreaseQuantity(productId) },
            onRemove = { productId -> viewModel.removeItem(productId) }
        )
        binding.rvCart.adapter = cartAdapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.cart.collect { cartItems ->
                cartAdapter.submitList(cartItems)
            }
        }

        lifecycleScope.launch {
            viewModel.totalAmount.collect { total ->
                binding.tvTotalAmount.text = "$${String.format("%.2f", total)}"
            }
        }

        lifecycleScope.launch {
            viewModel.availableProducts.collect { products ->
                productsAdapter.submitList(products)
            }
        }
    }

    /**
     * Configura el AutoCompleteTextView para mostrar la lista de clientes reales
     */
    private fun setupCustomerSelector() {
        lifecycleScope.launch {
            viewModel.customers.collect { customerList ->
                // Creamos un adaptador con los nombres de los clientes registrados
                val adapter = ArrayAdapter(
                    this@AddEditSaleActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    customerList.map { it.name }
                )
                binding.actvCustomer.setAdapter(adapter)

                // Al seleccionar un nombre, vinculamos el ID correspondiente
                binding.actvCustomer.setOnItemClickListener { _, _, position, _ ->
                    val selectedCustomerName = adapter.getItem(position)
                    val customer = customerList.find { it.name == selectedCustomerName }
                    selectedCustomerId = customer?.id

                    Toast.makeText(this@AddEditSaleActivity, "Seleccionado: ${customer?.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSaveSale.setOnClickListener {
            // Verificamos que se haya seleccionado un cliente de la lista
            val customerId = selectedCustomerId

            if (customerId == null) {
                Toast.makeText(this, "Por favor, selecciona un cliente de la lista", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.confirmSale(
                customerId = customerId,
                onSuccess = {
                    Toast.makeText(this, "¡Venta registrada con éxito!", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = { mensajeError ->
                    Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
                }
            )
        }

        binding.btnAddCustomer.setOnClickListener {
            showAddCustomerDialog()
        }
    }

    private fun showAddCustomerDialog() {
        val dialogBinding = DialogAddCustomerBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(this)
            .setTitle("Nuevo Cliente")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Guardar") { _, _ ->
                val name = dialogBinding.etDialogCustomerName.text.toString().trim()
                val phone = dialogBinding.etDialogCustomerPhone.text.toString().trim()
                val email = dialogBinding.etDialogCustomerEmail.text.toString().trim()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    viewModel.saveCustomer(name, phone, email)
                    Toast.makeText(this, "Cliente $name registrado", Toast.LENGTH_SHORT).show()
                    // La lista se actualizará sola gracias al Flow en el ViewModel
                } else {
                    Toast.makeText(this, "Nombre y teléfono son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }
}