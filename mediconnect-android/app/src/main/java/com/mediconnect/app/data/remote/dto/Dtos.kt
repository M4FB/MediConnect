package com.mediconnect.app.data.remote.dto

// General Response Wrappers
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class PageResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)

// Auth DTOs
data class LoginRequest(
    val email: String,
    val contrasena: String
)

data class RegisterRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val contrasena: String,
    val telefono: String?,
    val direccion: String?
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val user: UserDto
)

data class RefreshTokenRequest(
    val refreshToken: String
)

// User / Profile DTOs
data class UserDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val direccion: String?,
    val rol: String
)

data class UpdateUserRequest(
    val nombre: String,
    val apellido: String,
    val telefono: String?,
    val direccion: String?
)

data class ChangePasswordRequest(
    val contrasenaActual: String,
    val contrasenaNueva: String
)

// Doctor DTOs
data class DoctorDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val especialidad: String,
    val consultorio: String?,
    val telefono: String?,
    val email: String,
    val ratingPromedio: Double?
)

data class DoctorRegistrationRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val especialidad: String,
    val consultorio: String?,
    val telefono: String?
)

data class HorarioDisponibleDto(
    val id: Long,
    val doctorId: Long,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val disponible: Boolean
)

// Citas DTOs
data class CrearCitaRequest(
    val doctorId: Long,
    val fecha: String,
    val hora: String,
    val motivo: String
)

data class CitaDto(
    val id: Long,
    val doctorId: Long,
    val doctorNombre: String,
    val doctorApellido: String,
    val doctorEspecialidad: String,
    val pacienteId: Long,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val estado: String, // e.g. PENDIENTE, COMPLETADA, CANCELADA
    val codigoCheckIn: String?
)

data class CancelarCitaRequest(
    val motivo: String?
)

data class CheckInRequest(
    val codigoCheckIn: String
)

// Recetas DTOs
data class DetalleRecetaDto(
    val medicamento: String,
    val dosis: String,
    val frecuencia: String,
    val duracion: String
)

data class CrearRecetaRequest(
    val citaId: Long,
    val diagnostico: String,
    val indicaciones: String,
    val detalles: List<DetalleRecetaDto>
)

data class RecetaDto(
    val id: Long,
    val citaId: Long,
    val doctorNombre: String,
    val doctorApellido: String,
    val fecha: String,
    val diagnostico: String,
    val indicaciones: String,
    val detalles: List<DetalleRecetaDto>
)

// Historial Clinico DTOs
data class CrearHistorialRequest(
    val pacienteId: Long,
    val descripcion: String,
    val diagnostico: String,
    val tratamiento: String?
)

data class HistorialMedicoDto(
    val id: Long,
    val pacienteId: Long,
    val fecha: String,
    val doctorNombre: String,
    val doctorApellido: String,
    val descripcion: String,
    val diagnostico: String,
    val tratamiento: String?
)

// Notificaciones
data class NotificacionDto(
    val id: Long,
    val titulo: String,
    val mensaje: String,
    val fecha: String,
    val leido: Boolean
)

// Valoraciones
data class CrearValoracionRequest(
    val doctorId: Long,
    val puntuacion: Int,
    val comentario: String?
)

data class ValoracionDto(
    val id: Long,
    val doctorId: Long,
    val puntuacion: Int,
    val comentario: String?,
    val fecha: String
)
