package com.example.inventarioventas.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.databinding.ActivityAddEditProductBinding
import com.example.inventarioventas.ui.viewmodel.CategoryViewModel
import com.example.inventarioventas.ui.viewmodel.ProductViewModel
import com.example.inventarioventas.ui.viewmodel.factory.InventoryViewModelFactory
import com.example.inventarioventas.data.repository.RepositoryProvider

class AddEditProduct : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditProductBinding

    // ViewModels
    private lateinit var productVM: ProductViewModel
    private lateinit var categoryVM: CategoryViewModel

    private var productoAEditar: Product? = null

    // Variables para guardar los datos seleccionados
    private var selectedImageUri: String? = null
    private var selectedCategoryId: Int? = null

    // Lanzador para abrir la galería fotográfica
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Permiso de lectura persistente
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            selectedImageUri = uri.toString()
            binding.ivProductImage.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupCategorySelector() // Inicializamos el selector de categorías

        // Intentar recibir el producto para editar
        productoAEditar = intent.getParcelableExtra("EXTRA_PRODUCTO_EDITAR")

        if (productoAEditar != null) {
            llenarDatos(productoAEditar!!)
            binding.tvTitle.text = "Editar Producto"
            binding.btnSave.text = "Actualizar Cambios"
        }

        // Tocar la imagen abre la galería
        binding.ivProductImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSave.setOnClickListener {
            guardarProducto()
        }
    }

    private fun setupViewModel() {
        val repo = RepositoryProvider.provide(applicationContext)
        val factory = InventoryViewModelFactory(repo)

        // Inicializamos ambos ViewModels
        productVM = ViewModelProvider(this, factory)[ProductViewModel::class.java]
        categoryVM = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
    }

    // Configuración del menú desplegable usando LiveData (.observe)
    private fun setupCategorySelector() {
        categoryVM.categories.observe(this) { categoryList ->
            // Llenamos el menú con los nombres de las categorías
            val adapter = ArrayAdapter(
                this@AddEditProduct,
                android.R.layout.simple_dropdown_item_1line,
                categoryList.map { it.name }
            )
            binding.actvCategory.setAdapter(adapter)

            // Si estamos editando, mostramos su categoría actual en el menú
            productoAEditar?.let { prod ->
                val cat = categoryList.find { it.id == prod.categoryId }
                if (cat != null) {
                    binding.actvCategory.setText(cat.name, false)
                }
            }

            // Capturamos el ID de la categoría cuando el usuario selecciona una
            binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
                val selectedCategoryName = adapter.getItem(position)
                val category = categoryList.find { it.name == selectedCategoryName }
                selectedCategoryId = category?.id
            }
        }
    }

    private fun llenarDatos(p: Product) {
        binding.etName.setText(p.name)
        binding.etPrice.setText(p.price.toString())
        binding.etStock.setText(p.stock.toString())

        // Guardamos el ID por si el usuario no cambia la categoría
        selectedCategoryId = p.categoryId

        // Si el producto ya tenía foto, la cargamos
        if (p.imageUri != null) {
            selectedImageUri = p.imageUri
            binding.ivProductImage.setImageURI(Uri.parse(p.imageUri))
        }
    }

    private fun guardarProducto() {
        val name = binding.etName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val stockStr = binding.etStock.text.toString().trim()

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que se haya elegido una categoría
        val categoryIdToSave = selectedCategoryId
        if (categoryIdToSave == null) {
            Toast.makeText(this, "Por favor, selecciona una categoría", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val price = priceStr.toDouble()
            val stock = stockStr.toInt()

            if (productoAEditar == null) {
                // MODO AGREGAR
                val nuevo = Product(
                    id = 0,
                    name = name,
                    price = price,
                    stock = stock,
                    categoryId = categoryIdToSave,
                    imageUri = selectedImageUri
                )
                productVM.add(nuevo)
                Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
            } else {
                // MODO EDITAR
                val actualizado = productoAEditar!!.copy(
                    name = name,
                    price = price,
                    stock = stock,
                    categoryId = categoryIdToSave,
                    imageUri = selectedImageUri
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