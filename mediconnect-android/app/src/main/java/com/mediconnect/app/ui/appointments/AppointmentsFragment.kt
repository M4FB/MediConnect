package com.mediconnect.app.ui.appointments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mediconnect.app.R
import com.mediconnect.app.databinding.FragmentAppointmentsBinding
import com.mediconnect.app.ui.common.AppointmentsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppointmentsFragment : Fragment() {

    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppointmentsViewModel by viewModels()
    private lateinit var adapter: AppointmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btnCreateAppointment.setOnClickListener {
            findNavController().navigate(R.id.action_appointmentsFragment_to_appointmentCreateFragment)
        }

        lifecycleScope.launch {
            viewModel.appointmentsFlow.collectLatest { list ->
                adapter.submitList(list)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
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

    private fun setupRecyclerView() {
        adapter = AppointmentsAdapter(
            onCancelClick = { appointment ->
                val etMotivo = EditText(context).apply {
                    hint = "Ej. Incompatibilidad de horario"
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("Cancelar Cita")
                    .setMessage("Ingrese el motivo de la cancelación:")
                    .setView(etMotivo)
                    .setPositiveButton("Confirmar Cancelación") { dialog, _ ->
                        val motivo = etMotivo.text.toString().trim()
                        val finalMotivo = if (motivo.isEmpty()) "Cancelada por el paciente" else motivo
                        viewModel.cancelarCita(appointment.id, finalMotivo)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Volver", null)
                    .show()
            },
            onCheckInClick = { appointment ->
                val bundle = Bundle().apply {
                    putString("citaId", appointment.id)
                    putString("codigoCheckIn", appointment.codigoQr ?: "MC-CHECK-${appointment.id}")
                }
                findNavController().navigate(R.id.action_appointmentsFragment_to_appointmentCheckInFragment, bundle)
            }
        )
        binding.rvAppointments.layoutManager = LinearLayoutManager(context)
        binding.rvAppointments.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
