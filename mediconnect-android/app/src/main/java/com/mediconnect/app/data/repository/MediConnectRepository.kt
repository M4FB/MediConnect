package com.mediconnect.app.data.repository

import com.mediconnect.app.data.remote.dto.*
import kotlinx.coroutines.flow.Flow

interface MediConnectRepository {

    // Auth
    suspend fun login(email: String, contrasena: String): ApiResponse<AuthResponse>
    suspend fun register(
        nombre: String,
        apellido: String,
        email: String,
        contrasena: String,
        telefono: String?,
        direccion: String?
    ): ApiResponse<AuthResponse>
    suspend fun logout()
    suspend fun getSessionToken(): String?
    suspend fun saveSession(authResponse: AuthResponse)
    suspend fun saveCredentials(email: String, contrasena: String)
    fun getSavedEmail(): String?
    fun getSavedPassword(): String?
    fun setBiometricsEnabled(enabled: Boolean)
    fun isBiometricsEnabled(): Boolean

    // Profile
    suspend fun getProfile(): ApiResponse<UserDto>
    suspend fun updateProfile(
        nombre: String,
        apellido: String,
        telefono: String?,
        direccion: String?
    ): ApiResponse<UserDto>
    suspend fun changePassword(current: String, new: String): ApiResponse<Unit>

    // Doctors (Cache supported)
    fun getDoctorsFlow(): Flow<List<DoctorDto>>
    suspend fun fetchDoctors(): ApiResponse<List<DoctorDto>>
    suspend fun registerDoctor(
        nombre: String,
        apellido: String,
        email: String,
        especialidad: String,
        consultorio: String?,
        telefono: String?
    ): ApiResponse<DoctorDto>
    suspend fun getHorarios(doctorId: Long): ApiResponse<List<HorarioDisponibleDto>>

    // Appointments / Citas (Cache supported)
    fun getCitasFlow(): Flow<List<CitaDto>>
    suspend fun fetchCitas(): ApiResponse<List<CitaDto>>
    suspend fun crearCita(
        doctorId: Long,
        fecha: String,
        hora: String,
        motivo: String
    ): ApiResponse<CitaDto>
    suspend fun cancelarCita(citaId: Long, motivo: String?): ApiResponse<CitaDto>
    suspend fun checkIn(citaId: Long, code: String): ApiResponse<CitaDto>

    // Prescriptions / Recetas
    suspend fun getRecetas(): ApiResponse<List<RecetaDto>>
    suspend fun crearReceta(
        citaId: Long,
        diagnostico: String,
        indicaciones: String,
        detalles: List<DetalleRecetaDto>
    ): ApiResponse<RecetaDto>

    // Medical History
    suspend fun getHistorial(): ApiResponse<List<HistorialMedicoDto>>
    suspend fun crearHistorial(
        pacienteId: Long,
        descripcion: String,
        diagnostico: String,
        tratamiento: String?
    ): ApiResponse<HistorialMedicoDto>

    // Notifications
    suspend fun getNotificaciones(): ApiResponse<List<NotificacionDto>>
    suspend fun marcarNotificacionLeida(id: Long): ApiResponse<NotificacionDto>

    // Reviews
    suspend fun crearValoracion(
        doctorId: Long,
        rating: Int,
        comment: String?
    ): ApiResponse<ValoracionDto>
    suspend fun getValoraciones(doctorId: Long): ApiResponse<List<ValoracionDto>>
}
