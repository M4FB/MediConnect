package com.mediconnect.app.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.RecetaDto
import com.mediconnect.app.databinding.ItemPrescriptionBinding

class PrescriptionsAdapter(
    private val onRecetaClick: (RecetaDto) -> Unit
) : RecyclerView.Adapter<PrescriptionsAdapter.PrescriptionViewHolder>() {

    private var items: List<RecetaDto> = emptyList()

    fun submitList(list: List<RecetaDto>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val binding = ItemPrescriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PrescriptionViewHolder(private val binding: ItemPrescriptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(receta: RecetaDto) {
            binding.tvPrescDoctor.text = "Dr. ${receta.doctorNombre}"
            binding.tvPrescDate.text = "Fecha: ${receta.fechaEmision}"
            binding.tvPrescDiagnosis.text = "Diagnóstico: ${receta.diagnostico}"
            binding.root.setOnClickListener { onRecetaClick(receta) }
        }
    }
}
