package com.mediconnect.app.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediconnect.app.data.remote.dto.NotificacionDto
import com.mediconnect.app.data.repository.MediConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: MediConnectRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificacionDto>>(emptyList())
    val notifications: StateFlow<List<NotificacionDto>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getNotificaciones()
            if (response.success && response.data != null) {
                _notifications.value = response.data
            } else {
                _error.value = response.message ?: "Error al cargar notificaciones"
            }
            _isLoading.value = false
        }
    }

    fun marcarComoLeida(id: Long) {
        viewModelScope.launch {
            val response = repository.marcarNotificacionLeida(id)
            if (response.success) {
                loadNotifications()
            } else {
                _error.value = response.message ?: "Error al marcar como leída"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
