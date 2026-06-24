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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DoctorDetailFragment : Fragment() {

    private var _binding: FragmentDoctorDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DoctorsViewModel by viewModels()
    private lateinit var horariosAdapter: HorariosAdapter
    private lateinit var reviewsAdapter: ReviewsAdapter

    private var doctorId: String = ""
    private var doctorNombre: String = ""
    private var doctorEspecialidad: String = ""

    private val selectedFecha: String by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            doctorId = it.getString("doctorId") ?: ""
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
        viewModel.loadDoctorDetails(doctorId, selectedFecha)

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
                    viewModel.loadDoctorDetails(doctorId, selectedFecha)
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
                .setMessage("Ingrese el motivo de su consulta para la hora ${horario.hora}:")
                .setView(etMotivo)
                .setPositiveButton("Agendar") { dialog, _ ->
                    val motivo = etMotivo.text.toString().trim()
                    if (motivo.isNotEmpty()) {
                        val fechaHora = "${selectedFecha}T${horario.hora}"
                        viewModel.agendarCita(doctorId, fechaHora, motivo)
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
