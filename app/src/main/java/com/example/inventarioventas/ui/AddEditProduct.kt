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
import androidx.lifecycle.lifecycleScope
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.databinding.ActivityAddEditProductBinding
import com.example.inventarioventas.ui.viewmodel.CategoryViewModel
import com.example.inventarioventas.ui.viewmodel.ProductViewModel
import com.example.inventarioventas.ui.viewmodel.factory.InventoryViewModelFactory
import com.example.inventarioventas.data.repository.RepositoryProvider
import kotlinx.coroutines.launch

class AddEditProduct : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditProductBinding

    // ViewModels
    private lateinit var productVM: ProductViewModel
    private lateinit var categoryVM: CategoryViewModel

    private var productoAEditar: Product? = null

    // Variables para guardar los datos seleccionados
    private var selectedImageUri: String? = null
    private var selectedCategoryId: Int? = null

    // Lanzador para abrir la galería fotográfica y obtener el permiso de la imagen
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedImageUri = uri.toString()
            binding.ivProductImage.setImageURI(uri)
        }
    }

    /**
     * Función principal que se ejecuta al abrir la pantalla.
     * Configura la vista, inicializa herramientas y revisa si estamos en "Modo Edición".
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupCategorySelector()

        // Revisamos si venimos de la lista con un producto para editar
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

        // Botón principal de guardar
        binding.btnSave.setOnClickListener {
            guardarProducto()
        }
    }

    /**
     * Conecta esta pantalla con la base de datos (Repository) a través de los ViewModels.
     * Esto nos permite pedir y guardar datos sin congelar la pantalla.
     */
    private fun setupViewModel() {
        val repo = RepositoryProvider.provide(applicationContext)
        val factory = InventoryViewModelFactory(repo)

        productVM = ViewModelProvider(this, factory)[ProductViewModel::class.java]
        categoryVM = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
    }

    /**
     * Observa las categorías en la base de datos.
     * Si la base de datos está vacía, crea 10 categorías por defecto.
     * Luego, llena el menú desplegable para que el usuario pueda elegir una.
     */
    private fun setupCategorySelector() {
        categoryVM.categories.observe(this) { categoryList ->

            // 1. Si la lista está vacía, inicializamos las categorías por defecto automáticamente
            if (categoryList.isEmpty()) {
                lifecycleScope.launch {
                    val repo = RepositoryProvider.provide(applicationContext)
                    repo.inicializarCategoriasPorDefecto()
                }
                return@observe // Pausamos aquí. Al guardarse, el .observe se volverá a ejecutar automáticamente con la lista llena.
            }

            // 2. Llenamos el menú con los nombres de las categorías obtenidas
            val adapter = ArrayAdapter(
                this@AddEditProduct,
                android.R.layout.simple_dropdown_item_1line,
                categoryList.map { it.name }
            )
            binding.actvCategory.setAdapter(adapter)

            // 3. Si estamos editando un producto, mostramos su categoría actual en el menú
            productoAEditar?.let { prod ->
                val cat = categoryList.find { it.id == prod.categoryId }
                if (cat != null) {
                    binding.actvCategory.setText(cat.name, false)
                }
            }

            // 4. Capturamos el ID de la categoría cuando el usuario selecciona una opción
            binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
                val selectedCategoryName = adapter.getItem(position)
                val category = categoryList.find { it.name == selectedCategoryName }
                selectedCategoryId = category?.id
            }
        }
    }

    /**
     * Si estamos en "Modo Edición", esta función toma los datos del producto existente
     * y los pega en los campos de texto y en la imagen para que el usuario pueda modificarlos.
     */
    private fun llenarDatos(p: Product) {
        binding.etName.setText(p.name)
        binding.etPrice.setText(p.price.toString())
        binding.etStock.setText(p.stock.toString())

        // Guardamos el ID por si el usuario no cambia la categoría
        selectedCategoryId = p.categoryId

        // Si el producto ya tenía foto, la mostramos en la pantalla
        if (p.imageUri != null) {
            selectedImageUri = p.imageUri
            binding.ivProductImage.setImageURI(Uri.parse(p.imageUri))
        }
    }

    /**
     * Recolecta lo escrito por el usuario, valida que no haya campos vacíos
     * y guarda el producto (nuevo o editado) en la base de datos.
     */
    private fun guardarProducto() {
        val name = binding.etName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val stockStr = binding.etStock.text.toString().trim()

        // Validación 1: Textos no vacíos
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación 2: Categoría seleccionada
        val categoryIdToSave = selectedCategoryId
        if (categoryIdToSave == null) {
            Toast.makeText(this, "Por favor, selecciona una categoría", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val price = priceStr.toDouble()
            val stock = stockStr.toInt()

            if (productoAEditar == null) {
                // MODO AGREGAR: Creamos un producto desde cero (ID = 0 para que la BD le asigne uno)
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
                // MODO EDITAR: Copiamos el producto original y le actualizamos los valores nuevos
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

            // Cerramos esta pantalla y volvemos al inventario
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Error en el formato de datos", Toast.LENGTH_SHORT).show()
        }
    }
}