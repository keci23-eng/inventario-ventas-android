package com.example.inventarioventas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventarioventas.data.local.entity.Sale
import com.example.inventarioventas.databinding.ItemSaleBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalesAdapter(
    private var salesList: List<Sale>,
    private val onSaleClick: (Sale) -> Unit
) : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {

    // Usamos ViewBinding para conectar con tu nuevo item_sale.xml
    inner class SaleViewHolder(val binding: ItemSaleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val binding = ItemSaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = salesList[position]

        // 1. Número de Venta
        holder.binding.tvSaleId.text = "Venta #${sale.id}"

        // 2. Fecha (Convertimos los milisegundos a un formato de texto legible)
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateString = formatter.format(Date(sale.date))
        holder.binding.tvSaleDate.text = dateString

        // 3. Cliente (El plan decía mostrar el ID si aún no hay nombre)
        holder.binding.tvCustomerName.text = "Cliente ID: ${sale.customerId}"

        // 4. Cantidad de Artículos (Usamos un texto genérico temporal ya que Sale no tiene itemsCount)
        holder.binding.tvItemsCount.text = "Varios artículos"

        // 5. Total (Formateamos con 2 decimales)
        holder.binding.tvSaleTotal.text = "$${String.format("%.2f", sale.total)}"

        // Detectar el clic para abrir el detalle de la venta (Paso 1.5 del plan)
        holder.binding.root.setOnClickListener {
            onSaleClick(sale)
        }
    }

    override fun getItemCount(): Int = salesList.size

    // Función vital para actualizar la lista cuando el ViewModel traiga los datos
    fun updateList(newList: List<Sale>) {
        salesList = newList
        notifyDataSetChanged() // Refresca la lista en pantalla
    }
}