package com.example.inventarioventas.domain.cart

import com.example.inventarioventas.domain.model.CartItem

object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        cartItems.add(item)
    }

    fun removeItem(productId: Int) {
        cartItems.removeAll { it.productId == productId }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getItems(): List<CartItem> {
        return cartItems
    }

    fun getTotal(): Double {
        return cartItems.sumOf { it.price * it.quantity }
    }
}