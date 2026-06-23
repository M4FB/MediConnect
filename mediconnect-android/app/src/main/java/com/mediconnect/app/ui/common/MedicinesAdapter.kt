package com.mediconnect.app.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.DetalleRecetaDto
import com.mediconnect.app.databinding.ItemMedicineDetailBinding

class MedicinesAdapter : RecyclerView.Adapter<MedicinesAdapter.MedicineViewHolder>() {

    private var items: List<DetalleRecetaDto> = emptyList()

    fun submitList(list: List<DetalleRecetaDto>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val binding = ItemMedicineDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MedicineViewHolder(private val binding: ItemMedicineDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(med: DetalleRecetaDto) {
            binding.tvMedName.text = med.medicamento
            binding.tvMedDoseFreq.text = "Dosis: ${med.dosis} | Frecuencia: ${med.frecuencia}"
            binding.tvMedDuration.text = "Duración: ${med.duracion}"
        }
    }
}
