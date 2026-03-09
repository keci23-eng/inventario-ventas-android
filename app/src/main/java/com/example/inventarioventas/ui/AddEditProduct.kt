package com.example.inventarioventas.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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

    // Lanzador para pedir permiso de notificaciones (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador para abrir la galería fotográfica y obtener el permiso de la imagen
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedImageUri = uri.toString()
            binding.ivProductImage.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pedir permiso para notificaciones en Android 13 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setupViewModel()
        setupCategorySelector()

        productoAEditar = intent.getParcelableExtra("EXTRA_PRODUCTO_EDITAR")

        if (productoAEditar != null) {
            llenarDatos(productoAEditar!!)
            binding.tvTitle.text = "Editar Producto"
            binding.btnSave.text = "Actualizar Cambios"
        }

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

        productVM = ViewModelProvider(this, factory)[ProductViewModel::class.java]
        categoryVM = ViewModelProvider(this, factory)[CategoryViewModel::class.java]
    }

    private fun setupCategorySelector() {
        categoryVM.categories.observe(this) { categoryList ->
            if (categoryList.isEmpty()) {
                lifecycleScope.launch {
                    val repo = RepositoryProvider.provide(applicationContext)
                    repo.inicializarCategoriasPorDefecto()
                }
                return@observe
            }

            val adapter = ArrayAdapter(
                this@AddEditProduct,
                android.R.layout.simple_dropdown_item_1line,
                categoryList.map { it.name }
            )
            binding.actvCategory.setAdapter(adapter)

            productoAEditar?.let { prod ->
                val cat = categoryList.find { it.id == prod.categoryId }
                if (cat != null) {
                    binding.actvCategory.setText(cat.name, false)
                }
            }

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
        selectedCategoryId = p.categoryId

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

        val categoryIdToSave = selectedCategoryId
        if (categoryIdToSave == null) {
            Toast.makeText(this, "Por favor, selecciona una categoría", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val price = priceStr.toDouble()
            val stock = stockStr.toInt()

            if (productoAEditar == null) {
                val nuevo = Product(
                    id = 0,
                    name = name,
                    price = price,
                    stock = stock,
                    categoryId = categoryIdToSave,
                    imageUri = selectedImageUri
                )
                productVM.add(nuevo)
                mostrarNotificacion(name, "agregado") // Llamada a la notificación
            } else {
                val actualizado = productoAEditar!!.copy(
                    name = name,
                    price = price,
                    stock = stock,
                    categoryId = categoryIdToSave,
                    imageUri = selectedImageUri
                )
                productVM.update(actualizado)
                mostrarNotificacion(name, "actualizado") // Llamada a la notificación
            }

            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Error en el formato de datos", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Crea y muestra una notificación en la barra superior del dispositivo.
     */
    private fun mostrarNotificacion(nombreProducto: String, accion: String) {
        val channelId = "inventario_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Los canales son obligatorios desde Android 8.0 (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Inventario",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones al crear o editar productos"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Construir la notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_save) // Ícono por defecto de Android
            .setContentTitle("Producto $accion")
            .setContentText("El producto '$nombreProducto' se guardó correctamente.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Se borra al tocarla
            .build()

        // Lanzar la notificación (usamos un ID único basado en el tiempo)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}