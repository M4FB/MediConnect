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
import androidx.recyclerview.widget.LinearLayoutManager
import com.mediconnect.app.databinding.FragmentDoctorDetailBinding
import com.mediconnect.app.ui.common.HorariosAdapter
import com.mediconnect.app.ui.common.ReviewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DoctorDetailFragment : Fragment() {

    private var _binding: FragmentDoctorDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DoctorsViewModel by viewModels()
    private lateinit var horariosAdapter: HorariosAdapter
    private lateinit var reviewsAdapter: ReviewsAdapter

    private var doctorId: Long = 0
    private var doctorNombre: String = ""
    private var doctorEspecialidad: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            doctorId = it.getLong("doctorId")
            doctorNombre = it.getString("doctorNombre") ?: ""
            doctorEspecialidad = it.getString("doctorEspecialidad") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDetailName.text = doctorNombre
        binding.tvDetailSpecialty.text = doctorEspecialidad
        binding.tvDetailContact.text = "Cargando contacto e información..."

        setupRecyclerViews()
        viewModel.loadDoctorDetails(doctorId)

        binding.btnSubmitReview.setOnClickListener {
            val ratingStr = binding.spinnerRating.selectedItem.toString()
            val comment = binding.etReviewComment.text.toString().trim()
            val rating = ratingStr.toIntOrNull() ?: 5

            viewModel.addReview(doctorId, rating, comment)
            binding.etReviewComment.text.clear()
            Toast.makeText(context, "Valoración enviada", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            viewModel.horarios.collectLatest { list ->
                horariosAdapter.submitList(list)
            }
        }

        lifecycleScope.launch {
            viewModel.reviews.collectLatest { list ->
                reviewsAdapter.submitList(list)
            }
        }

        lifecycleScope.launch {
            viewModel.bookingSuccess.collectLatest { appointment ->
                if (appointment != null) {
                    Toast.makeText(context, "¡Cita agendada exitosamente!", Toast.LENGTH_LONG).show()
                    viewModel.clearBookingState()
                    viewModel.loadDoctorDetails(doctorId)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.pbHorarios.visibility = if (loading) View.VISIBLE else View.GONE
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

    private fun setupRecyclerViews() {
        horariosAdapter = HorariosAdapter { horario ->
            val etMotivo = EditText(context).apply {
                hint = "Ej. Chequeo general"
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Agendar Cita")
                .setMessage("Ingrese el motivo de su consulta para la fecha ${horario.fecha}:")
                .setView(etMotivo)
                .setPositiveButton("Agendar") { dialog, _ ->
                    val motivo = etMotivo.text.toString().trim()
                    if (motivo.isNotEmpty()) {
                        viewModel.agendarCita(doctorId, horario.fecha, horario.horaInicio, motivo)
                    } else {
                        Toast.makeText(context, "El motivo es requerido", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }

        binding.rvHorarios.layoutManager = LinearLayoutManager(context)
        binding.rvHorarios.adapter = horariosAdapter

        reviewsAdapter = ReviewsAdapter()
        binding.rvReviews.layoutManager = LinearLayoutManager(context)
        binding.rvReviews.adapter = reviewsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
