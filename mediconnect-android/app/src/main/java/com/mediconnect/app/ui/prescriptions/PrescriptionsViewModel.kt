package com.mediconnect.app.ui.prescriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediconnect.app.data.remote.dto.RecetaDto
import com.mediconnect.app.data.repository.MediConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrescriptionsViewModel @Inject constructor(
    private val repository: MediConnectRepository
) : ViewModel() {

    private val _prescriptions = MutableStateFlow<List<RecetaDto>>(emptyList())
    val prescriptions: StateFlow<List<RecetaDto>> = _prescriptions.asStateFlow()

    private val _selectedPrescription = MutableStateFlow<RecetaDto?>(null)
    val selectedPrescription: StateFlow<RecetaDto?> = _selectedPrescription.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPrescriptions() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getRecetas()
            if (response.success && response.data != null) {
                _prescriptions.value = response.data
            } else {
                _error.value = response.message ?: "Error al cargar recetas"
            }
            _isLoading.value = false
        }
    }

    fun loadPrescriptionDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getRecetas()
            if (response.success && response.data != null) {
                val found = response.data.find { it.id == id }
                _selectedPrescription.value = found
            } else {
                _error.value = response.message ?: "Error al cargar detalle de receta"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
