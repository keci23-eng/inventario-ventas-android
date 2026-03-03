package com.example.inventarioventas.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventarioventas.R
import com.example.inventarioventas.databinding.ActivitySalesBinding

class SalesActivity : AppCompatActivity() {
    // 1. Declaramos la variable de Binding
    private lateinit var binding: ActivitySalesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inflamos la vista y la conectamos
        binding = ActivitySalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Aplicamos los márgenes a la raíz (evita el error de findViewById)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. ¡La magia del botón! Al hacer clic, viajamos a AddEditSaleActivity
        binding.fabAddSale.setOnClickListener {
            val intent = Intent(this, AddEditSale::class.java)
            startActivity(intent)
        }
    }
}