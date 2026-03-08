package com.example.inventarioventas.adapter

import android.net.Uri // <-- NUEVO: Necesario para leer la ruta de la imagen
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioventas.databinding.ItemIventoryBinding
import com.example.inventarioventas.data.local.entity.Product

class ProductAdapter(
    private var productList: List<Product>,
    // Esta función nos permitirá hacer clic en un producto para editarlo después
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemIventoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemIventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Le pasamos los 3 datos a la tarjeta
        holder.binding.tvProductName.text = product.name
        holder.binding.tvProductPrice.text = "$${product.price}"
        holder.binding.tvStockBadge.text = "Stock: ${product.stock}"

        // --- NUEVO: Lógica para mostrar la imagen ---
        if (!product.imageUri.isNullOrEmpty()) {
            try {
                // Transformamos el texto (String) a una Uri real y la mostramos
                holder.binding.ivItemImage.setImageURI(Uri.parse(product.imageUri))
            } catch (e: Exception) {
                // Si la imagen fue borrada del teléfono o hay error, mostramos el ícono por defecto
                holder.binding.ivItemImage.setImageResource(android.R.drawable.ic_menu_camera)
            }
        } else {
            // Si el producto no tiene foto guardada en la base de datos, mostramos el ícono
            holder.binding.ivItemImage.setImageResource(android.R.drawable.ic_menu_camera)
        }
        // -------------------------------------------

        // Detectar el clic
        holder.binding.root.setOnClickListener {
            onProductClick(product)
        }
    }

    // Función clave para actualizar la lista cuando Room detecta cambios
    fun updateList(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }

    fun getProductAt(position: Int): Product {
        return productList[position]
    }
}