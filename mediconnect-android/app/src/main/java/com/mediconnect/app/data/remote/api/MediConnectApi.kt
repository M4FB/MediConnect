package com.mediconnect.app.data.remote.api

import com.mediconnect.app.data.remote.dto.*
import retrofit2.http.*

interface MediConnectApi {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<AuthResponse>

    // Profile (Mapped to /api/users/)
    @GET("users/me")
    suspend fun getProfile(): ApiResponse<UserDto>

    @PUT("users/me")
    suspend fun updateProfile(@Body request: UpdateUserRequest): ApiResponse<UserDto>

    @PUT("users/me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>

    // Doctors (Mapped to /api/doctors/)
    @GET("doctors")
    suspend fun getDoctors(
        @Query("search") search: String? = null,
        @Query("especialidad") especialidad: String? = null
    ): ApiResponse<List<DoctorDto>>

    @GET("doctors/{id}")
    suspend fun getDoctorById(@Path("id") id: String): ApiResponse<DoctorDto>

    @GET("doctors/{id}/horarios-disponibles")
    suspend fun getHorariosDisponibles(
        @Path("id") doctorId: String,
        @Query("fecha") fecha: String // YYYY-MM-DD
    ): ApiResponse<List<HorarioDisponibleDto>>

    // Admin (Mapped to /api/admin/doctores)
    @POST("admin/doctores")
    suspend fun registerDoctor(@Body request: DoctorRegistrationRequest): ApiResponse<DoctorDto>

    @GET("admin/users")
    suspend fun getAllUsers(): ApiResponse<List<UserDto>>

    @PUT("admin/users/{id}/toggle-active")
    suspend fun toggleUserActive(@Path("id") id: String): ApiResponse<UserDto>

    // Appointments (Citas) (Mapped to /api/citas)
    @GET("citas")
    suspend fun getCitas(@Query("estado") estado: String? = null): ApiResponse<List<CitaDto>>

    @POST("citas")
    suspend fun crearCita(@Body request: CrearCitaRequest): ApiResponse<CitaDto>

    @PUT("citas/{id}/cancelar")
    suspend fun cancelarCita(
        @Path("id") citaId: String,
        @Body request: CancelarCitaRequest
    ): ApiResponse<CitaDto>

    @POST("citas/{id}/check-in")
    suspend fun checkIn(
        @Path("id") citaId: String,
        @Body request: CheckInRequest
    ): ApiResponse<CitaDto>

    // Prescriptions (Recetas) (Mapped to /api/recetas)
    @GET("recetas/mis-recetas")
    suspend fun getRecetas(): ApiResponse<List<RecetaDto>>

    @POST("recetas")
    suspend fun crearReceta(@Body request: CrearRecetaRequest): ApiResponse<RecetaDto>

    // Medical History (Mapped to /api/historial)
    @GET("historial")
    suspend fun getHistorial(): ApiResponse<List<HistorialMedicoDto>>

    @POST("historial")
    suspend fun crearHistorial(@Body request: CrearHistorialRequest): ApiResponse<HistorialMedicoDto>

    // Notifications (Mapped to /api/notificaciones)
    @GET("notificaciones")
    suspend fun getNotificaciones(): ApiResponse<List<NotificacionDto>>

    @PUT("notificaciones/{id}/leer")
    suspend fun marcarLeida(@Path("id") id: String): ApiResponse<Void>

    // Reviews (Valoraciones) (Mapped under /api/doctors/)
    @POST("doctors/{id}/valoraciones")
    suspend fun crearValoracion(
        @Path("id") doctorId: String,
        @Body request: CrearValoracionRequest
    ): ApiResponse<ValoracionDto>

    @GET("doctors/{id}/valoraciones")
    suspend fun getValoraciones(@Path("id") doctorId: String): ApiResponse<List<ValoracionDto>>
}
