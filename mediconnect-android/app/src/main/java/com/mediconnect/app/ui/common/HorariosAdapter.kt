package com.mediconnect.app.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.HorarioDisponibleDto
import com.mediconnect.app.databinding.ItemHorarioBinding

class HorariosAdapter(
    private val onBookClick: (HorarioDisponibleDto) -> Unit
) : RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder>() {

    private var items: List<HorarioDisponibleDto> = emptyList()

    fun submitList(list: List<HorarioDisponibleDto>) {
        items = list.filter { it.disponible }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val binding = ItemHorarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class HorarioViewHolder(private val binding: ItemHorarioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(horario: HorarioDisponibleDto) {
            binding.tvHorarioDateTime.text = "Disponible a las: ${horario.hora}"
            binding.btnBookHorario.setOnClickListener { onBookClick(horario) }
        }
    }
}
