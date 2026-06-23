package com.mediconnect.app.ui.doctors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediconnect.app.data.remote.dto.CitaDto
import com.mediconnect.app.data.remote.dto.DoctorDto
import com.mediconnect.app.data.remote.dto.HorarioDisponibleDto
import com.mediconnect.app.data.remote.dto.ValoracionDto
import com.mediconnect.app.data.repository.MediConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorsViewModel @Inject constructor(
    private val repository: MediConnectRepository
) : ViewModel() {

    val doctorsFlow = repository.getDoctorsFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _horarios = MutableStateFlow<List<HorarioDisponibleDto>>(emptyList())
    val horarios: StateFlow<List<HorarioDisponibleDto>> = _horarios.asStateFlow()

    private val _reviews = MutableStateFlow<List<ValoracionDto>>(emptyList())
    val reviews: StateFlow<List<ValoracionDto>> = _reviews.asStateFlow()

    private val _bookingSuccess = MutableStateFlow<CitaDto?>(null)
    val bookingSuccess: StateFlow<CitaDto?> = _bookingSuccess.asStateFlow()

    init {
        refreshDoctors()
    }

    fun refreshDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.fetchDoctors()
            if (!response.success) {
                _error.value = response.message
            }
            _isLoading.value = false
        }
    }

    fun registerDoctor(
        nombre: String,
        apellido: String,
        email: String,
        especialidad: String,
        consultorio: String?,
        telefono: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.registerDoctor(nombre, apellido, email, especialidad, consultorio, telefono)
            if (response.success) {
                refreshDoctors()
            } else {
                _error.value = response.message ?: "Error al registrar doctor"
            }
            _isLoading.value = false
        }
    }

    fun loadDoctorDetails(doctorId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val hResponse = repository.getHorarios(doctorId)
            if (hResponse.success && hResponse.data != null) {
                _horarios.value = hResponse.data
            }
            val rResponse = repository.getValoraciones(doctorId)
            if (rResponse.success && rResponse.data != null) {
                _reviews.value = rResponse.data
            }
            _isLoading.value = false
        }
    }

    fun addReview(doctorId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            val response = repository.crearValoracion(doctorId, rating, comment)
            if (response.success) {
                loadDoctorDetails(doctorId)
            } else {
                _error.value = response.message ?: "Error al enviar reseña"
            }
        }
    }

    fun agendarCita(doctorId: Long, fecha: String, hora: String, motivo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.crearCita(doctorId, fecha, hora, motivo)
            if (response.success && response.data != null) {
                _bookingSuccess.value = response.data
            } else {
                _error.value = response.message ?: "Error al agendar cita"
            }
            _isLoading.value = false
        }
    }

    fun clearBookingState() {
        _bookingSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
