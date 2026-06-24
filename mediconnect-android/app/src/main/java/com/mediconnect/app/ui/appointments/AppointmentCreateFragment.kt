package com.mediconnect.app.ui.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mediconnect.app.data.remote.dto.DoctorDto
import com.mediconnect.app.databinding.FragmentAppointmentCreateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppointmentCreateFragment : Fragment() {

    private var _binding: FragmentAppointmentCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppointmentsViewModel by viewModels()
    private var doctorList: List<DoctorDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.doctorsFlow.collectLatest { list ->
                doctorList = list
                val names = list.map { "Dr. ${it.nombre} ${it.apellido} (${it.especialidad})" }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerDoctor.adapter = adapter
            }
        }

        binding.btnSubmitAppt.setOnClickListener {
            val selectedPos = binding.spinnerDoctor.selectedItemPosition
            if (selectedPos < 0 || selectedPos >= doctorList.size) {
                Toast.makeText(context, "Por favor seleccione un doctor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val doctor = doctorList[selectedPos]
            val date = binding.etApptDate.text.toString().trim()
            val time = binding.etApptTime.text.toString().trim()
            val motivo = binding.etApptMotivo.text.toString().trim()

            if (date.isEmpty() || time.isEmpty() || motivo.isEmpty()) {
                Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Combine date and time into a single LocalDateTime ISO 8601 string
            val fechaHora = "${date}T${time}:00"
            viewModel.crearCita(doctor.id, fechaHora, motivo)
        }

        lifecycleScope.launch {
            viewModel.createSuccess.collectLatest { success ->
                if (success) {
                    Toast.makeText(context, "Cita agendada con éxito", Toast.LENGTH_SHORT).show()
                    viewModel.resetCreateState()
                    findNavController().navigateUp()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                binding.btnSubmitAppt.isEnabled = !loading
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
