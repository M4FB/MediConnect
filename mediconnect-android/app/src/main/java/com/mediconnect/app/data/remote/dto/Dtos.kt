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
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val nombre: String,
    val apellido: String,
    val telefono: String?,
    val fechaNacimiento: String?, // YYYY-MM-DD
    val genero: String?,
    val direccion: String?,
    val grupoSanguineo: String?,
    val alergias: String?
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
    val id: String, // UUID
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val role: String, // Matches "role" in backend JSON
    val activo: Boolean,
    val fotoUrl: String?,
    val createdAt: String?
)

data class UpdateUserRequest(
    val nombre: String,
    val apellido: String,
    val telefono: String?,
    val fotoUrl: String?
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

// Doctor DTOs
data class DoctorDto(
    val id: String, // UUID
    val userId: String, // UUID
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val fotoUrl: String?,
    val especialidad: String,
    val numeroColegiado: String,
    val descripcion: String?,
    val horarioInicio: String?,
    val horarioFin: String?,
    val costoCita: Double,
    val promedioValoracion: Double?,
    val totalValoraciones: Long?
)

data class DoctorRegistrationRequest(
    val email: String,
    val password: String,
    val nombre: String,
    val apellido: String,
    val telefono: String?,
    val especialidad: String,
    val numeroColegiado: String,
    val descripcion: String?,
    val costoCita: Double,
    val horarioInicio: String?,
    val horarioFin: String?,
    val diasAtencion: String?
)

data class HorarioDisponibleDto(
    val hora: String, // LocalTime
    val disponible: Boolean
)

// Citas DTOs
data class CrearCitaRequest(
    val doctorId: String, // UUID
    val fechaHora: String, // LocalDateTime
    val motivo: String,
    val notas: String?
)

data class CitaDto(
    val id: String, // UUID
    val pacienteId: String, // UUID
    val pacienteNombre: String,
    val doctorId: String, // UUID
    val doctorNombre: String,
    val doctorEspecialidad: String,
    val fechaHora: String, // LocalDateTime
    val motivo: String,
    val notas: String?,
    val estado: String, // e.g. PENDIENTE, CONFIRMADA, EN_CURSO, COMPLETADA, CANCELADA
    val codigoQr: String?,
    val notasCancelacion: String?,
    val createdAt: String?
)

data class CancelarCitaRequest(
    val motivo: String
)

data class CheckInRequest(
    val codigoQr: String
)

// Recetas DTOs
data class DetalleRecetaDto(
    val id: String, // UUID
    val medicamento: String,
    val dosis: String,
    val frecuencia: String,
    val duracion: String,
    val instrucciones: String?
)

data class CrearRecetaRequest(
    val citaId: String, // UUID
    val diagnostico: String,
    val observaciones: String?,
    val detalles: List<DetalleRequest>
)

data class DetalleRequest(
    val medicamento: String,
    val dosis: String,
    val frecuencia: String,
    val duracion: String,
    val instrucciones: String?
)

data class RecetaDto(
    val id: String, // UUID
    val citaId: String, // UUID
    val doctorId: String, // UUID
    val doctorNombre: String,
    val pacienteId: String, // UUID
    val pacienteNombre: String,
    val diagnostico: String,
    val observaciones: String?,
    val fechaEmision: String, // LocalDateTime/LocalDate
    val detalles: List<DetalleRecetaDto>,
    val createdAt: String?
)

// Historial Clinico DTOs
data class CrearHistorialRequest(
    val tipo: String, // TipoHistorial enum: CONSULTA, CIRUGIA, EXAMEN, VACUNA, OTRO
    val titulo: String,
    val descripcion: String,
    val fecha: String?, // LocalDateTime
    val doctorNombre: String?,
    val archivoUrl: String?
)

data class HistorialMedicoDto(
    val id: String, // UUID
    val pacienteId: String, // UUID
    val tipo: String,
    val titulo: String,
    val descripcion: String,
    val fecha: String, // LocalDateTime
    val doctorNombre: String?,
    val archivoUrl: String?,
    val createdAt: String?
)

// Notificaciones
data class NotificacionDto(
    val id: String, // UUID
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val leida: Boolean, // Matches "leida" in backend JSON
    val createdAt: String?
)

// Valoraciones
data class CrearValoracionRequest(
    val doctorId: String?, // UUID
    val calificacion: Int, // Matches "calificacion" in backend JSON
    val comentario: String?
)

data class ValoracionDto(
    val id: String, // UUID
    val pacienteId: String, // UUID
    val pacienteNombre: String,
    val doctorId: String, // UUID
    val calificacion: Int,
    val comentario: String?,
    val createdAt: String?
)
