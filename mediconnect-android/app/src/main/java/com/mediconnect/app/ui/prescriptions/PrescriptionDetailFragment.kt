package com.mediconnect.app.ui.prescriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mediconnect.app.databinding.FragmentPrescriptionDetailBinding
import com.mediconnect.app.ui.common.MedicinesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrescriptionDetailFragment : Fragment() {

    private var _binding: FragmentPrescriptionDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PrescriptionsViewModel by viewModels()
    private lateinit var adapter: MedicinesAdapter
    private var recetaId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recetaId = it.getString("recetaId") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrescriptionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.loadPrescriptionDetail(recetaId)

        lifecycleScope.launch {
            viewModel.selectedPrescription.collectLatest { receta ->
                if (receta != null) {
                    binding.tvDetailPrescDoctor.text = "Dr. ${receta.doctorNombre}"
                    binding.tvDetailPrescDate.text = "Fecha de Emisión: ${receta.fechaEmision}"
                    binding.tvDetailPrescDiagnosis.text = receta.diagnostico
                    binding.tvDetailPrescInstructions.text = receta.observaciones ?: ""
                    adapter.submitList(receta.detalles)
                }
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
        adapter = MedicinesAdapter()
        binding.rvMedicines.layoutManager = LinearLayoutManager(context)
        binding.rvMedicines.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
