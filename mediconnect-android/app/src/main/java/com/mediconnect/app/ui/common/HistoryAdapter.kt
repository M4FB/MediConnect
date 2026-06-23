package com.mediconnect.app.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.HistorialMedicoDto
import com.mediconnect.app.databinding.ItemHistoryBinding

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var items: List<HistorialMedicoDto> = emptyList()

    fun submitList(list: List<HistorialMedicoDto>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hist: HistorialMedicoDto) {
            binding.tvHistDate.text = "Fecha: ${hist.fecha}"
            binding.tvHistDoctor.text = "Dr/Dra: ${hist.doctorNombre} ${hist.doctorApellido}"
            binding.tvHistDiagnosis.text = "Diagnóstico: ${hist.diagnostico}"
            binding.tvHistDescription.text = "Descripción: ${hist.descripcion}"
            binding.tvHistTreatment.text = "Tratamiento: ${hist.tratamiento ?: "Ninguno"}"
        }
    }
}
