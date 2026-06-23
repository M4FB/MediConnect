package com.mediconnect.app.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.DoctorDto
import com.mediconnect.app.databinding.ItemDoctorBinding

class DoctorsAdapter(
    private val onDoctorClick: (DoctorDto) -> Unit
) : RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>() {

    private var items: List<DoctorDto> = emptyList()

    fun submitList(list: List<DoctorDto>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class DoctorViewHolder(private val binding: ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorDto) {
            binding.tvDoctorName.text = "Dr. ${doctor.nombre} ${doctor.apellido}"
            binding.tvDoctorSpecialty.text = doctor.especialidad
            val rating = doctor.ratingPromedio?.let { "Calificación: $it/5.0" } ?: "Sin calificaciones"
            binding.tvDoctorRating.text = "$rating | Consultorio: ${doctor.consultorio ?: "N/A"}"
            binding.root.setOnClickListener { onDoctorClick(doctor) }
        }
    }
}
