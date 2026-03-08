package com.example.inventarioventas.data.remote.firebase.model


import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageService {

    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadProductImage(productId: Int, imageUri: Uri): String {
        val ref = storage.reference.child("product_images/product_$productId.jpg")

        ref.putFile(imageUri).await()

        return ref.downloadUrl.await().toString()
    }
}