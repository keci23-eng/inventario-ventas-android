package com.example.inventarioventas.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.databinding.ItemProductCartBinding

class ProductCartAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductCartAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ItemProductCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "$${String.format("%.2f", product.price)}"

            // Lógica para mostrar el stock y deshabilitar clics si está agotado
            if (product.stock <= 0) {
                binding.tvProductStock.text = "Agotado"
                binding.tvProductStock.setTextColor(Color.RED)
                binding.root.alpha = 0.5f // Lo hace ver "apagado"
                binding.root.setOnClickListener(null) // Quita el clic
            } else {
                binding.tvProductStock.text = "Stock: ${product.stock}"
                binding.tvProductStock.setTextColor(Color.parseColor("#757575")) // Gris original
                binding.root.alpha = 1.0f // Opacidad normal

                // Si hay stock, permitimos que al darle clic se envíe al carrito
                binding.root.setOnClickListener {
                    onProductClick(product)
                }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}