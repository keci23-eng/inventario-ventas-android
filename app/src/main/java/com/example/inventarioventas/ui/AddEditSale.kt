package com.example.inventarioventas.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventarioventas.R
import com.example.inventarioventas.databinding.ActivityAddEditSaleBinding

class AddEditSale : AppCompatActivity() {
    // 1. Declaramos la variable de Binding
    private lateinit var binding: ActivityAddEditSaleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inflamos el layout usando el Binding
        binding = ActivityAddEditSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Aplicamos los insets directamente a binding.root (evita el error de findViewById)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. (Opcional por ahora) Aquí configuraremos el botón de guardar
        binding.btnSaveSale.setOnClickListener {
            // Aquí luego tomaremos los datos de los campos de texto

            // finish() cierra esta pantalla y te regresa a la anterior (Ventas)
            finish()
        }
    }
}