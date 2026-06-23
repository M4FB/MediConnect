package com.mediconnect.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mediconnect.app.databinding.FragmentHistoryCreateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryCreateFragment : Fragment() {

    private var _binding: FragmentHistoryCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.patientId.collectLatest { id ->
                if (id != null) {
                    binding.etHistPacienteId.setText(id.toString())
                }
            }
        }

        binding.btnSubmitHist.setOnClickListener {
            val pacienteIdStr = binding.etHistPacienteId.text.toString().trim()
            val diagnosis = binding.etHistDiagnosis.text.toString().trim()
            val description = binding.etHistDescription.text.toString().trim()
            val treatment = binding.etHistTreatment.text.toString().trim().ifEmpty { null }

            val pacienteId = pacienteIdStr.toLongOrNull()

            if (pacienteId == null || diagnosis.isEmpty() || description.isEmpty()) {
                Toast.makeText(context, "Los campos ID de Paciente, Diagnóstico y Descripción son requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.crearHistorial(pacienteId, description, diagnosis, treatment)
        }

        lifecycleScope.launch {
            viewModel.createSuccess.collectLatest { success ->
                if (success) {
                    Toast.makeText(context, "Registro agregado al historial médico", Toast.LENGTH_SHORT).show()
                    viewModel.resetCreateState()
                    findNavController().navigateUp()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                binding.btnSubmitHist.isEnabled = !loading
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
