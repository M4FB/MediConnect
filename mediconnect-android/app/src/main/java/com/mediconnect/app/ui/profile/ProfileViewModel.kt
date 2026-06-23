package com.mediconnect.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediconnect.app.data.remote.dto.UserDto
import com.mediconnect.app.data.repository.MediConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: MediConnectRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserDto?>(null)
    val userProfile: StateFlow<UserDto?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val _passwordSuccess = MutableStateFlow(false)
    val passwordSuccess: StateFlow<Boolean> = _passwordSuccess.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getProfile()
            if (response.success && response.data != null) {
                _userProfile.value = response.data
            } else {
                _error.value = response.message ?: "Error al cargar perfil"
            }
            _isLoading.value = false
        }
    }

    fun updateProfile(nombre: String, apellido: String, telefono: String?, direccion: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.updateProfile(nombre, apellido, telefono, direccion)
            if (response.success && response.data != null) {
                _userProfile.value = response.data
                _updateSuccess.value = true
            } else {
                _error.value = response.message ?: "Error al actualizar perfil"
            }
            _isLoading.value = false
        }
    }

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.changePassword(current, new)
            if (response.success) {
                _passwordSuccess.value = true
            } else {
                _error.value = response.message ?: "Error al cambiar contraseña"
            }
            _isLoading.value = false
        }
    }

    fun isBiometricsEnabled(): Boolean {
        return repository.isBiometricsEnabled()
    }

    fun setBiometricsEnabled(enabled: Boolean) {
        repository.setBiometricsEnabled(enabled)
    }

    fun resetUpdateState() {
        _updateSuccess.value = false
    }

    fun resetPasswordState() {
        _passwordSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
