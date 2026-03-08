package com.example.inventarioventas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioventas.databinding.ItemCartRowBinding
import com.example.inventarioventas.domain.model.CartItem

class CartAdapter(
    private val onIncrease: (Int) -> Unit,
    private val onDecrease: (Int) -> Unit,
    private val onRemove: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
    }

    inner class CartViewHolder(private val binding: ItemCartRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            // Mostramos el nombre del producto
            binding.tvCartProductName.text = cartItem.productName

            // Mostramos el subtotal de ese producto (Precio x Cantidad)
            binding.tvCartProductPrice.text = "$${String.format("%.2f", cartItem.subtotal)}"

            // Mostramos la cantidad actual
            binding.tvCartQuantity.text = cartItem.quantity.toString()

            // Conectamos los botones a las funciones que le pasaremos desde la Activity
            binding.btnIncrease.setOnClickListener {
                onIncrease(cartItem.productId)
            }

            binding.btnDecrease.setOnClickListener {
                onDecrease(cartItem.productId)
            }

            binding.btnRemoveItem.setOnClickListener {
                onRemove(cartItem.productId)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // Son el mismo si tienen el mismo ID de producto
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // Verificamos si cambió algo (como la cantidad o el subtotal)
            return oldItem == newItem
        }
    }
}