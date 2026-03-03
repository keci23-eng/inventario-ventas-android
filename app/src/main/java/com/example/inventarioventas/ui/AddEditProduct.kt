package com.example.inventarioventas.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventarioventas.R
import com.example.inventarioventas.databinding.ActivityAddEditProductBinding

class AddEditProduct : AppCompatActivity() {
    // 1. Declaramos la variable de Binding
    private lateinit var binding: ActivityAddEditProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inflamos la vista
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Aplicamos los márgenes de pantalla completa directamente a binding.root
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}