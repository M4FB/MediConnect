package com.mediconnect.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediconnect.app.data.remote.dto.AuthResponse
import com.mediconnect.app.data.repository.MediConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MediConnectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, contrasena: String) {
        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            val result = repository.login(email, contrasena)
            if (result.success && result.data != null) {
                repository.saveCredentials(email, contrasena)
                _state.value = AuthState(authResponse = result.data)
            } else {
                _state.value = AuthState(error = result.message ?: "Credenciales inválidas")
            }
        }
    }

    fun register(
        nombre: String,
        apellido: String,
        email: String,
        contrasena: String,
        telefono: String?,
        direccion: String?
    ) {
        viewModelScope.launch {
            _state.value = AuthState(isLoading = true)
            val result = repository.register(nombre, apellido, email, contrasena, telefono, direccion)
            if (result.success && result.data != null) {
                repository.saveCredentials(email, contrasena)
                _state.value = AuthState(authResponse = result.data, isRegistered = true)
            } else {
                _state.value = AuthState(error = result.message ?: "Error al registrarse")
            }
        }
    }

    fun loginWithBiometrics() {
        viewModelScope.launch {
            val email = repository.getSavedEmail()
            val pass = repository.getSavedPassword()
            if (email != null && pass != null) {
                login(email, pass)
            } else {
                _state.value = AuthState(error = "No hay credenciales guardadas para biometría")
            }
        }
    }

    fun isBiometricsEnabled(): Boolean {
        return repository.isBiometricsEnabled()
    }

    fun setBiometricsEnabled(enabled: Boolean) {
        repository.setBiometricsEnabled(enabled)
    }

    fun hasSavedCredentials(): Boolean {
        return repository.getSavedEmail() != null && repository.getSavedPassword() != null
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    data class AuthState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val authResponse: AuthResponse? = null,
        val isRegistered: Boolean = false
    )
}
