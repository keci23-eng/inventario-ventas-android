package com.example.inventarioventas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioventas.databinding.ItemOnlineProductBinding
// 1. CAMBIAMOS EL IMPORT AL MODELO CORRECTO
import com.example.inventarioventas.domain.model.OnlineProduct

class OnlineProductAdapter(
    // 2. CAMBIAMOS EL TIPO DE LA LISTA Y DE LA FUNCIÓN
    private var onlineProducts: List<OnlineProduct>,
    private val onImportClick: (OnlineProduct) -> Unit
) : RecyclerView.Adapter<OnlineProductAdapter.OnlineViewHolder>() {

    inner class OnlineViewHolder(val binding: ItemOnlineProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineViewHolder {
        val binding = ItemOnlineProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnlineViewHolder(binding)
    }

    override fun getItemCount(): Int = onlineProducts.size

    override fun onBindViewHolder(holder: OnlineViewHolder, position: Int) {
        val product = onlineProducts[position]

        // NOTA: Si '.title' sale en rojo, cámbialo por '.name' (depende de cómo tu compañero nombró la variable en OnlineProduct)
        holder.binding.tvOnlineProductName.text = product.title

        holder.binding.tvOnlineProductCategory.text = "Categoría: ${product.category}"

        holder.binding.tvOnlineProductPrice.text = "$${product.price}"

        holder.binding.btnImport.setOnClickListener {
            onImportClick(product)
        }
    }

    // 3. CAMBIAMOS EL TIPO EN UPDATE LIST
    fun updateList(newList: List<OnlineProduct>) {
        onlineProducts = newList
        notifyDataSetChanged()
    }
}