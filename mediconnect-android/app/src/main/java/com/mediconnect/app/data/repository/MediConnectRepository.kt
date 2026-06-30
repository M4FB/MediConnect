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
        fotoUrl: String?
    ): ApiResponse<UserDto>
    suspend fun changePassword(current: String, new: String): ApiResponse<Unit>

    // Doctors (Cache supported)
    fun getDoctorsFlow(): Flow<List<DoctorDto>>
    suspend fun fetchDoctors(): ApiResponse<List<DoctorDto>>
    suspend fun registerDoctor(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        telefono: String?,
        especialidad: String,
        numeroColegiado: String,
        descripcion: String?,
        costoCita: Double,
        horarioInicio: String?,
        horarioFin: String?,
        diasAtencion: String?
    ): ApiResponse<DoctorDto>
    suspend fun getHorarios(doctorId: String, fecha: String): ApiResponse<List<HorarioDisponibleDto>>
    suspend fun getAllUsers(): ApiResponse<List<UserDto>>
    suspend fun toggleUserActive(userId: String): ApiResponse<UserDto>

    // Appointments / Citas (Cache supported)
    fun getCitasFlow(): Flow<List<CitaDto>>
    suspend fun fetchCitas(): ApiResponse<List<CitaDto>>
    suspend fun crearCita(
        doctorId: String,
        fechaHora: String,
        motivo: String,
        notas: String?
    ): ApiResponse<CitaDto>
    suspend fun cancelarCita(citaId: String, motivo: String): ApiResponse<CitaDto>
    suspend fun checkIn(citaId: String, code: String): ApiResponse<CitaDto>

    // Prescriptions / Recetas
    suspend fun getRecetas(): ApiResponse<List<RecetaDto>>
    suspend fun crearReceta(
        citaId: String,
        diagnostico: String,
        observaciones: String?,
        detalles: List<DetalleRequest>
    ): ApiResponse<RecetaDto>

    // Medical History
    suspend fun getHistorial(): ApiResponse<List<HistorialMedicoDto>>
    suspend fun crearHistorial(
        tipo: String,
        titulo: String,
        descripcion: String,
        fecha: String?,
        doctorNombre: String?,
        archivoUrl: String?
    ): ApiResponse<HistorialMedicoDto>

    // Notifications
    suspend fun getNotificaciones(): ApiResponse<List<NotificacionDto>>
    suspend fun marcarNotificacionLeida(id: String): ApiResponse<Void>

    // Reviews
    suspend fun crearValoracion(
        doctorId: String,
        calificacion: Int,
        comentario: String?
    ): ApiResponse<ValoracionDto>
    suspend fun getValoraciones(doctorId: String): ApiResponse<List<ValoracionDto>>
}
