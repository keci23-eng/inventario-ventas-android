package com.example.inventarioventas.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.inventarioventas.data.local.entity.Sale
import com.example.inventarioventas.data.local.entity.SaleItem

data class SaleWithItems(
    @Embedded val sale: Sale,

    @Relation(
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val items: List<SaleItem>
)