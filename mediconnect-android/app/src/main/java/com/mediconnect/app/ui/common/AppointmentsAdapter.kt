package com.mediconnect.app.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediconnect.app.data.remote.dto.CitaDto
import com.mediconnect.app.databinding.ItemAppointmentBinding

class AppointmentsAdapter(
    private val onCancelClick: (CitaDto) -> Unit,
    private val onCheckInClick: (CitaDto) -> Unit
) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    private var items: List<CitaDto> = emptyList()

    fun submitList(list: List<CitaDto>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class AppointmentViewHolder(private val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cita: CitaDto) {
            binding.tvApptDoctor.text = "Dr. ${cita.doctorNombre} ${cita.doctorApellido} (${cita.doctorEspecialidad})"
            binding.tvApptDateTime.text = "Fecha: ${cita.fecha} | Hora: ${cita.hora}"
            binding.tvApptMotivo.text = "Motivo: ${cita.motivo}"
            binding.tvApptStatus.text = "Estado: ${cita.estado}"

            if (cita.estado.uppercase() == "PENDIENTE") {
                binding.btnCancelAppt.visibility = View.VISIBLE
                binding.btnCheckInAppt.visibility = View.VISIBLE
                binding.btnCancelAppt.setOnClickListener { onCancelClick(cita) }
                binding.btnCheckInAppt.setOnClickListener { onCheckInClick(cita) }
            } else {
                binding.btnCancelAppt.visibility = View.GONE
                binding.btnCheckInAppt.visibility = View.GONE
            }
        }
    }
}
