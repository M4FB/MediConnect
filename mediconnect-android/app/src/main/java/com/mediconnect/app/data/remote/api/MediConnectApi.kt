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

    // Profile
    @GET("usuarios/perfil")
    suspend fun getProfile(): ApiResponse<UserDto>

    @PUT("usuarios/perfil")
    suspend fun updateProfile(@Body request: UpdateUserRequest): ApiResponse<UserDto>

    @PUT("usuarios/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>

    // Doctors
    @GET("doctores")
    suspend fun getDoctors(): ApiResponse<List<DoctorDto>>

    @POST("doctores")
    suspend fun registerDoctor(@Body request: DoctorRegistrationRequest): ApiResponse<DoctorDto>

    @GET("doctores/{id}/horarios")
    suspend fun getHorariosDisponibles(@Path("id") doctorId: Long): ApiResponse<List<HorarioDisponibleDto>>

    // Appointments (Citas)
    @GET("citas")
    suspend fun getCitas(): ApiResponse<List<CitaDto>>

    @POST("citas")
    suspend fun crearCita(@Body request: CrearCitaRequest): ApiResponse<CitaDto>

    @PUT("citas/{id}/cancelar")
    suspend fun cancelarCita(
        @Path("id") citaId: Long,
        @Body request: CancelarCitaRequest
    ): ApiResponse<CitaDto>

    @POST("citas/{id}/checkin")
    suspend fun checkIn(
        @Path("id") citaId: Long,
        @Body request: CheckInRequest
    ): ApiResponse<CitaDto>

    // Prescriptions (Recetas)
    @GET("recetas")
    suspend fun getRecetas(): ApiResponse<List<RecetaDto>>

    @POST("recetas")
    suspend fun crearReceta(@Body request: CrearRecetaRequest): ApiResponse<RecetaDto>

    // Medical History
    @GET("historial-clinico")
    suspend fun getHistorial(): ApiResponse<List<HistorialMedicoDto>>

    @POST("historial-clinico")
    suspend fun crearHistorial(@Body request: CrearHistorialRequest): ApiResponse<HistorialMedicoDto>

    // Notifications
    @GET("notificaciones")
    suspend fun getNotificaciones(): ApiResponse<List<NotificacionDto>>

    @PUT("notificaciones/{id}/leer")
    suspend fun marcarLeida(@Path("id") id: Long): ApiResponse<NotificacionDto>

    // Reviews (Valoraciones)
    @POST("valoraciones")
    suspend fun crearValoracion(@Body request: CrearValoracionRequest): ApiResponse<ValoracionDto>

    @GET("valoraciones/doctor/{id}")
    suspend fun getValoraciones(@Path("id") doctorId: Long): ApiResponse<List<ValoracionDto>>
}
