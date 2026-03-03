package com.example.inventarioventas.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventarioventas.databinding.ActivityInventoryBinding
import com.example.inventarioventas.ui.SalesActivity


class InventoryActivity : AppCompatActivity() {
    // 1. Declaramos la variable del binding
    private lateinit var binding: ActivityInventoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inflamos el layout
        binding = ActivityInventoryBinding.inflate(layoutInflater)

        // 3. Establecemos la vista raíz
        setContentView(binding.root)

        // 4. Usamos binding.root en lugar de findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddEditProduct::class.java)
            startActivity(intent)
        }

    }
}