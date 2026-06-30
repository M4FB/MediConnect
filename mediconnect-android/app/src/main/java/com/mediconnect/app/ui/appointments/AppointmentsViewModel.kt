package com.mediconnect.app.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediconnect.app.data.remote.dto.CitaDto
import com.mediconnect.app.data.remote.dto.UserDto
import com.mediconnect.app.data.repository.MediConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val repository: MediConnectRepository
) : ViewModel() {

    val appointmentsFlow = repository.getCitasFlow()
    val doctorsFlow = repository.getDoctorsFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess.asStateFlow()

    private val _checkInSuccess = MutableStateFlow<CitaDto?>(null)
    val checkInSuccess: StateFlow<CitaDto?> = _checkInSuccess.asStateFlow()

    private val _userProfile = MutableStateFlow<UserDto?>(null)
    val userProfile: StateFlow<UserDto?> = _userProfile.asStateFlow()

    init {
        refreshAppointments()
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val response = repository.getProfile()
            if (response.success && response.data != null) {
                _userProfile.value = response.data
            }
        }
    }

    fun refreshAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.fetchCitas()
            if (!response.success) {
                _error.value = response.message
            }
            val docResponse = repository.fetchDoctors()
            if (!docResponse.success && _error.value == null) {
                _error.value = docResponse.message
            }
            _isLoading.value = false
        }
    }

    fun crearCita(doctorId: String, fechaHora: String, motivo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.crearCita(doctorId, fechaHora, motivo, null)
            if (response.success) {
                _createSuccess.value = true
                refreshAppointments()
            } else {
                _error.value = response.message ?: "Error al programar cita"
            }
            _isLoading.value = false
        }
    }

    fun cancelarCita(citaId: String, motivo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.cancelarCita(citaId, motivo)
            if (response.success) {
                refreshAppointments()
            } else {
                _error.value = response.message ?: "Error al cancelar cita"
            }
            _isLoading.value = false
        }
    }

    fun checkIn(citaId: String, code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.checkIn(citaId, code)
            if (response.success && response.data != null) {
                _checkInSuccess.value = response.data
                refreshAppointments()
            } else {
                _error.value = response.message ?: "Error al realizar check-in"
            }
            _isLoading.value = false
        }
    }

    fun resetCreateState() {
        _createSuccess.value = false
    }

    fun resetCheckInState() {
        _checkInSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
