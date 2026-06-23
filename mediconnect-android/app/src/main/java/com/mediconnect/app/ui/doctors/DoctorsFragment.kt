package com.mediconnect.app.ui.doctors

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
import com.mediconnect.app.databinding.FragmentDoctorsBinding
import com.mediconnect.app.ui.common.DoctorsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DoctorsFragment : Fragment() {

    private var _binding: FragmentDoctorsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DoctorsViewModel by viewModels()
    private lateinit var adapter: DoctorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btnRegisterDoctor.setOnClickListener {
            showRegisterDoctorDialog()
        }

        lifecycleScope.launch {
            viewModel.doctorsFlow.collectLatest { list ->
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
        adapter = DoctorsAdapter { doctor ->
            val bundle = Bundle().apply {
                putLong("doctorId", doctor.id)
                putString("doctorNombre", "${doctor.nombre} ${doctor.apellido}")
                putString("doctorEspecialidad", doctor.especialidad)
            }
            findNavController().navigate(R.id.action_doctorsFragment_to_doctorDetailFragment, bundle)
        }
        binding.rvDoctors.layoutManager = LinearLayoutManager(context)
        binding.rvDoctors.adapter = adapter
    }

    private fun showRegisterDoctorDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_register_doctor, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etDocNombre)
        val etApellido = dialogView.findViewById<EditText>(R.id.etDocApellido)
        val etEmail = dialogView.findViewById<EditText>(R.id.etDocEmail)
        val etEspecialidad = dialogView.findViewById<EditText>(R.id.etDocEspecialidad)
        val etConsultorio = dialogView.findViewById<EditText>(R.id.etDocConsultorio)
        val etTelefono = dialogView.findViewById<EditText>(R.id.etDocTelefono)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Registrar") { dialog, _ ->
                val nombre = etNombre.text.toString().trim()
                val apellido = etApellido.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val especialidad = etEspecialidad.text.toString().trim()
                val consultorio = etConsultorio.text.toString().trim().ifEmpty { null }
                val telefono = etTelefono.text.toString().trim().ifEmpty { null }

                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || especialidad.isEmpty()) {
                    Toast.makeText(context, "Los campos Nombre, Apellido, Correo y Especialidad son requeridos", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.registerDoctor(nombre, apellido, email, especialidad, consultorio, telefono)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
