package com.mediconnect.app.data.repository

import android.content.SharedPreferences
import com.mediconnect.app.data.local.dao.CitaDao
import com.mediconnect.app.data.local.dao.DoctorDao
import com.mediconnect.app.data.local.entity.CitaCache
import com.mediconnect.app.data.local.entity.DoctorCache
import com.mediconnect.app.data.remote.api.MediConnectApi
import com.mediconnect.app.data.remote.dto.*
import com.mediconnect.app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediConnectRepositoryImpl @Inject constructor(
    private val api: MediConnectApi,
    private val doctorDao: DoctorDao,
    private val citaDao: CitaDao,
    private val sharedPreferences: SharedPreferences
) : MediConnectRepository {

    private suspend fun <T> safeApiCall(call: suspend () -> ApiResponse<T>): ApiResponse<T> {
        return try {
            call()
        } catch (e: Exception) {
            ApiResponse(success = false, message = e.localizedMessage ?: "Network error occurred", data = null)
        }
    }

    // Auth
    override suspend fun login(email: String, contrasena: String): ApiResponse<AuthResponse> {
        val response = safeApiCall { api.login(LoginRequest(email, contrasena)) }
        if (response.success && response.data != null) {
            saveSession(response.data)
        }
        return response
    }

    override suspend fun register(
        nombre: String,
        apellido: String,
        email: String,
        contrasena: String,
        telefono: String?,
        direccion: String?
    ): ApiResponse<AuthResponse> {
        val response = safeApiCall {
            api.register(RegisterRequest(nombre, apellido, email, contrasena, telefono, direccion))
        }
        if (response.success && response.data != null) {
            saveSession(response.data)
        }
        return response
    }

    override suspend fun logout() {
        sharedPreferences.edit()
            .remove(Constants.KEY_ACCESS_TOKEN)
            .remove(Constants.KEY_REFRESH_TOKEN)
            .apply()
        doctorDao.clearDoctors()
        citaDao.clearCitas()
    }

    override suspend fun getSessionToken(): String? {
        return sharedPreferences.getString(Constants.KEY_ACCESS_TOKEN, null)
    }

    override suspend fun saveSession(authResponse: AuthResponse) {
        sharedPreferences.edit()
            .putString(Constants.KEY_ACCESS_TOKEN, authResponse.accessToken)
            .putString(Constants.KEY_REFRESH_TOKEN, authResponse.refreshToken)
            .apply()
    }

    override suspend fun saveCredentials(email: String, contrasena: String) {
        sharedPreferences.edit()
            .putString(Constants.KEY_USER_EMAIL, email)
            .putString(Constants.KEY_USER_PASSWORD, contrasena)
            .apply()
    }

    override fun getSavedEmail(): String? {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, null)
    }

    override fun getSavedPassword(): String? {
        return sharedPreferences.getString(Constants.KEY_USER_PASSWORD, null)
    }

    override fun setBiometricsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(Constants.KEY_BIOMETRICS_ENABLED, enabled)
            .apply()
    }

    override fun isBiometricsEnabled(): Boolean {
        return sharedPreferences.getBoolean(Constants.KEY_BIOMETRICS_ENABLED, false)
    }

    // Profile
    override suspend fun getProfile(): ApiResponse<UserDto> {
        return safeApiCall { api.getProfile() }
    }

    override suspend fun updateProfile(
        nombre: String,
        apellido: String,
        telefono: String?,
        direccion: String?
    ): ApiResponse<UserDto> {
        return safeApiCall { api.updateProfile(UpdateUserRequest(nombre, apellido, telefono, direccion)) }
    }

    override suspend fun changePassword(current: String, new: String): ApiResponse<Unit> {
        return safeApiCall { api.changePassword(ChangePasswordRequest(current, new)) }
    }

    // Doctors
    override fun getDoctorsFlow(): Flow<List<DoctorDto>> {
        return doctorDao.getDoctorsFlow().map { cacheList ->
            cacheList.map {
                DoctorDto(it.id, it.nombre, it.apellido, it.especialidad, it.consultorio, it.telefono, it.email, it.ratingPromedio)
            }
        }
    }

    override suspend fun fetchDoctors(): ApiResponse<List<DoctorDto>> {
        val response = safeApiCall { api.getDoctors() }
        if (response.success && response.data != null) {
            val cacheList = response.data.map {
                DoctorCache(it.id, it.nombre, it.apellido, it.especialidad, it.consultorio, it.telefono, it.email, it.ratingPromedio)
            }
            doctorDao.clearDoctors()
            doctorDao.insertDoctors(cacheList)
        }
        return response
    }

    override suspend fun registerDoctor(
        nombre: String,
        apellido: String,
        email: String,
        especialidad: String,
        consultorio: String?,
        telefono: String?
    ): ApiResponse<DoctorDto> {
        return safeApiCall {
            api.registerDoctor(DoctorRegistrationRequest(nombre, apellido, email, especialidad, consultorio, telefono))
        }
    }

    override suspend fun getHorarios(doctorId: Long): ApiResponse<List<HorarioDisponibleDto>> {
        return safeApiCall { api.getHorariosDisponibles(doctorId) }
    }

    // Appointments (Citas)
    override fun getCitasFlow(): Flow<List<CitaDto>> {
        return citaDao.getCitasFlow().map { cacheList ->
            cacheList.map {
                CitaDto(it.id, it.doctorId, it.doctorNombre, it.doctorApellido, it.doctorEspecialidad, it.pacienteId, it.fecha, it.hora, it.motivo, it.estado, it.codigoCheckIn)
            }
        }
    }

    override suspend fun fetchCitas(): ApiResponse<List<CitaDto>> {
        val response = safeApiCall { api.getCitas() }
        if (response.success && response.data != null) {
            val cacheList = response.data.map {
                CitaCache(it.id, it.doctorId, it.doctorNombre, it.doctorApellido, it.doctorEspecialidad, it.pacienteId, it.fecha, it.hora, it.motivo, it.estado, it.codigoCheckIn)
            }
            citaDao.clearCitas()
            citaDao.insertCitas(cacheList)
        }
        return response
    }

    override suspend fun crearCita(
        doctorId: Long,
        fecha: String,
        hora: String,
        motivo: String
    ): ApiResponse<CitaDto> {
        return safeApiCall { api.crearCita(CrearCitaRequest(doctorId, fecha, hora, motivo)) }
    }

    override suspend fun cancelarCita(citaId: Long, motivo: String?): ApiResponse<CitaDto> {
        return safeApiCall { api.cancelarCita(citaId, CancelarCitaRequest(motivo)) }
    }

    override suspend fun checkIn(citaId: Long, code: String): ApiResponse<CitaDto> {
        return safeApiCall { api.checkIn(citaId, CheckInRequest(code)) }
    }

    // Prescriptions (Recetas)
    override suspend fun getRecetas(): ApiResponse<List<RecetaDto>> {
        return safeApiCall { api.getRecetas() }
    }

    override suspend fun crearReceta(
        citaId: Long,
        diagnostico: String,
        indicaciones: String,
        detalles: List<DetalleRecetaDto>
    ): ApiResponse<RecetaDto> {
        return safeApiCall { api.crearReceta(CrearRecetaRequest(citaId, diagnostico, indicaciones, detalles)) }
    }

    // Medical History
    override suspend fun getHistorial(): ApiResponse<List<HistorialMedicoDto>> {
        return safeApiCall { api.getHistorial() }
    }

    override suspend fun crearHistorial(
        pacienteId: Long,
        descripcion: String,
        diagnostico: String,
        tratamiento: String?
    ): ApiResponse<HistorialMedicoDto> {
        return safeApiCall { api.crearHistorial(CrearHistorialRequest(pacienteId, descripcion, diagnostico, tratamiento)) }
    }

    // Notifications
    override suspend fun getNotificaciones(): ApiResponse<List<NotificacionDto>> {
        return safeApiCall { api.getNotificaciones() }
    }

    override suspend fun marcarNotificacionLeida(id: Long): ApiResponse<NotificacionDto> {
        return safeApiCall { api.marcarLeida(id) }
    }

    // Reviews
    override suspend fun crearValoracion(
        doctorId: Long,
        rating: Int,
        comment: String?
    ): ApiResponse<ValoracionDto> {
        return safeApiCall { api.crearValoracion(CrearValoracionRequest(doctorId, rating, comment)) }
    }

    override suspend fun getValoraciones(doctorId: Long): ApiResponse<List<ValoracionDto>> {
        return safeApiCall { api.getValoraciones(doctorId) }
    }
}
