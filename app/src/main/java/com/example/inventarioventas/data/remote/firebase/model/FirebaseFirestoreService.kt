package com.example.inventarioventas.data.remote.firebase.model


import com.example.inventarioventas.data.local.entity.Category
import com.example.inventarioventas.data.local.entity.Customer
import com.example.inventarioventas.data.local.entity.Product
import com.example.inventarioventas.data.local.entity.Sale
import com.example.inventarioventas.data.local.entity.SaleItem
import com.example.inventarioventas.data.remote.firebase.model.CategoryFirebase
import com.example.inventarioventas.data.remote.firebase.model.CustomerFirebase
import com.example.inventarioventas.data.remote.firebase.model.ProductFirebase
import com.example.inventarioventas.data.remote.firebase.model.SaleFirebase
import com.example.inventarioventas.data.remote.firebase.model.SaleItemFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreService {

    private val db = FirebaseFirestore.getInstance()

    suspend fun uploadCategory(category: Category) {
        val data = CategoryFirebase(
            id = category.id,
            name = category.name
        )

        db.collection("categories")
            .document(category.id.toString())
            .set(data)
            .await()
    }

    suspend fun uploadProduct(product: Product, imageUrl: String? = null) {
        val data = ProductFirebase(
            id = product.id,
            name = product.name,
            price = product.price,
            stock = product.stock,
            categoryId = product.categoryId,
            imageUrl = imageUrl
        )

        db.collection("products")
            .document(product.id.toString())
            .set(data)
            .await()
    }

    suspend fun uploadCustomer(customer: Customer) {
        val data = CustomerFirebase(
            id = customer.id,
            name = customer.name,
            phone = customer.phone,
            email = customer.email
        )

        db.collection("customers")
            .document(customer.id.toString())
            .set(data)
            .await()
    }

    suspend fun uploadSale(sale: Sale) {
        val data = SaleFirebase(
            id = sale.id,
            customerId = sale.customerId,
            date = sale.date,
            total = sale.total
        )

        db.collection("sales")
            .document(sale.id.toString())
            .set(data)
            .await()
    }

    suspend fun uploadSaleItem(item: SaleItem) {
        val docId = "${item.saleId}_${item.productId}"

        val data = SaleItemFirebase(
            saleId = item.saleId,
            productId = item.productId,
            quantity = item.quantity,
            unitPrice = item.unitPrice
        )

        db.collection("sale_items")
            .document(docId)
            .set(data)
            .await()
    }

    suspend fun deleteCategory(id: Int) {
        db.collection("categories")
            .document(id.toString())
            .delete()
            .await()
    }

    suspend fun deleteProduct(id: Int) {
        db.collection("products")
            .document(id.toString())
            .delete()
            .await()
    }

    suspend fun deleteCustomer(id: Int) {
        db.collection("customers")
            .document(id.toString())
            .delete()
            .await()
    }
}