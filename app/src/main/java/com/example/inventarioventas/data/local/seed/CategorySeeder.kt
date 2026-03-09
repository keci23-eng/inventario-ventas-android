package com.example.inventarioventas.data.local.seed

import com.example.inventarioventas.data.local.dao.CategoryDao
import com.example.inventarioventas.data.local.entity.Category

class CategorySeeder(
    private val categoryDao: CategoryDao
) {

    suspend fun seedDefaultCategories() {
        val count = categoryDao.countCategories()

        if (count == 0) {
            val defaultCategories = listOf(
                Category(name = "Ropa"),
                Category(name = "Joyería"),
                Category(name = "Zapatos"),
                Category(name = "Accesorios"),
                Category(name = "Tecnología"),
                Category(name = "Hogar"),
                Category(name = "Belleza"),
                Category(name = "Papelería"),
                Category(name = "Deportes"),
                Category(name = "Alimentos")
            )

            defaultCategories.forEach { category ->
                categoryDao.insert(category)
            }
        }
    }
}