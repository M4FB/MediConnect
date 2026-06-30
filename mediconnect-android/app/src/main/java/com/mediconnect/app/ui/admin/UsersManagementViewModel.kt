package com.mediconnect.app.ui.admin

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
class UsersManagementViewModel @Inject constructor(
    private val repository: MediConnectRepository
) : ViewModel() {

    private val _usersList = MutableStateFlow<List<UserDto>>(emptyList())
    val usersList: StateFlow<List<UserDto>> = _usersList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getAllUsers()
            if (response.success && response.data != null) {
                _usersList.value = response.data
            } else {
                _error.value = response.message ?: "Error al obtener usuarios"
            }
            _isLoading.value = false
        }
    }

    fun toggleUserActive(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.toggleUserActive(userId)
            if (response.success && response.data != null) {
                _usersList.value = _usersList.value.map {
                    if (it.id == userId) response.data else it
                }
            } else {
                _error.value = response.message ?: "Error al modificar estado"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
