package com.example.inventarioventas.domain.model

data class CartItem(
    val productId: Int,
    val productName: String,
    var quantity: Int = 1, // 'var' nos permite actualizar la cantidad, y le damos 1 por defecto
    val price: Double
) {
    // Esta variable se calcula sola cada vez que la llamas
    val subtotal: Double get() = price * quantity
}