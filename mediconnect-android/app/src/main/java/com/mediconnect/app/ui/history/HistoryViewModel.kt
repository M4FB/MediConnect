package com.mediconnect.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediconnect.app.data.remote.dto.HistorialMedicoDto
import com.mediconnect.app.data.repository.MediConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: MediConnectRepository
) : ViewModel() {

    private val _histories = MutableStateFlow<List<HistorialMedicoDto>>(emptyList())
    val histories: StateFlow<List<HistorialMedicoDto>> = _histories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess.asStateFlow()

    private val _patientId = MutableStateFlow<Long?>(null)
    val patientId: StateFlow<Long?> = _patientId.asStateFlow()

    init {
        loadHistory()
        loadPatientId()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getHistorial()
            if (response.success && response.data != null) {
                _histories.value = response.data
            } else {
                _error.value = response.message ?: "Error al obtener historial clínico"
            }
            _isLoading.value = false
        }
    }

    private fun loadPatientId() {
        viewModelScope.launch {
            val response = repository.getProfile()
            if (response.success && response.data != null) {
                _patientId.value = response.data.id
            }
        }
    }

    fun crearHistorial(pacienteId: Long, descripcion: String, diagnostico: String, tratamiento: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.crearHistorial(pacienteId, descripcion, diagnostico, tratamiento)
            if (response.success) {
                _createSuccess.value = true
                loadHistory()
            } else {
                _error.value = response.message ?: "Error al agregar historial clínico"
            }
            _isLoading.value = false
        }
    }

    fun resetCreateState() {
        _createSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
