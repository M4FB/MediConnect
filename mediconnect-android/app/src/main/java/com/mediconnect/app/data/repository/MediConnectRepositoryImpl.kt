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
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBodyString = e.response()?.errorBody()?.string()
                if (!errorBodyString.isNullOrEmpty()) {
                    val gson = com.google.gson.Gson()
                    val errorResponse = gson.fromJson(errorBodyString, ApiResponse::class.java)
                    ApiResponse(
                        success = false,
                        message = errorResponse.message ?: "Error del servidor (${e.code()})",
                        data = null
                    )
                } else {
                    ApiResponse(success = false, message = "Error del servidor (${e.code()})", data = null)
                }
            } catch (jsonEx: Exception) {
                ApiResponse(success = false, message = "Error del servidor: ${e.message()}", data = null)
            }
        } catch (e: Exception) {
            ApiResponse(success = false, message = e.localizedMessage ?: "Ocurrió un error de red", data = null)
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
            api.register(
                RegisterRequest(
                    email = email,
                    password = contrasena,
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    fechaNacimiento = null,
                    genero = null,
                    direccion = direccion,
                    grupoSanguineo = null,
                    alergias = null
                )
            )
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
        fotoUrl: String?
    ): ApiResponse<UserDto> {
        return safeApiCall { api.updateProfile(UpdateUserRequest(nombre, apellido, telefono, fotoUrl)) }
    }

    override suspend fun changePassword(current: String, new: String): ApiResponse<Unit> {
        return safeApiCall { api.changePassword(ChangePasswordRequest(current, new)) }
    }

    // Doctors
    override fun getDoctorsFlow(): Flow<List<DoctorDto>> {
        return doctorDao.getDoctorsFlow().map { cacheList ->
            cacheList.map {
                DoctorDto(
                    id = it.id,
                    userId = "",
                    nombre = it.nombre,
                    apellido = it.apellido,
                    email = it.email,
                    telefono = it.telefono,
                    fotoUrl = null,
                    especialidad = it.especialidad,
                    numeroColegiado = "",
                    descripcion = null,
                    horarioInicio = null,
                    horarioFin = null,
                    costoCita = 0.0,
                    promedioValoracion = it.promedioValoracion,
                    totalValoraciones = null
                )
            }
        }
    }

    override suspend fun fetchDoctors(): ApiResponse<List<DoctorDto>> {
        val response = safeApiCall { api.getDoctors() }
        if (response.success && response.data != null) {
            val cacheList = response.data.map {
                DoctorCache(
                    id = it.id,
                    nombre = it.nombre,
                    apellido = it.apellido,
                    especialidad = it.especialidad,
                    telefono = it.telefono,
                    email = it.email,
                    promedioValoracion = it.promedioValoracion
                )
            }
            doctorDao.clearDoctors()
            doctorDao.insertDoctors(cacheList)
        }
        return response
    }

    override suspend fun registerDoctor(
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
    ): ApiResponse<DoctorDto> {
        return safeApiCall {
            api.registerDoctor(
                DoctorRegistrationRequest(
                    email = email,
                    password = password,
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    especialidad = especialidad,
                    numeroColegiado = numeroColegiado,
                    descripcion = descripcion,
                    costoCita = costoCita,
                    horarioInicio = horarioInicio,
                    horarioFin = horarioFin,
                    diasAtencion = diasAtencion
                )
            )
        }
    }

    override suspend fun getHorarios(doctorId: String, fecha: String): ApiResponse<List<HorarioDisponibleDto>> {
        return safeApiCall { api.getHorariosDisponibles(doctorId, fecha) }
    }

    // Appointments (Citas)
    override fun getCitasFlow(): Flow<List<CitaDto>> {
        return citaDao.getCitasFlow().map { cacheList ->
            cacheList.map {
                CitaDto(
                    id = it.id,
                    pacienteId = it.pacienteId,
                    pacienteNombre = "",
                    doctorId = it.doctorId,
                    doctorNombre = it.doctorNombre,
                    doctorEspecialidad = it.doctorEspecialidad,
                    fechaHora = it.fechaHora,
                    motivo = it.motivo,
                    notas = null,
                    estado = it.estado,
                    codigoQr = it.codigoQr,
                    notasCancelacion = null,
                    createdAt = null
                )
            }
        }
    }

    override suspend fun fetchCitas(): ApiResponse<List<CitaDto>> {
        val response = safeApiCall { api.getCitas() }
        if (response.success && response.data != null) {
            val cacheList = response.data.map {
                CitaCache(
                    id = it.id,
                    doctorId = it.doctorId,
                    doctorNombre = it.doctorNombre,
                    doctorEspecialidad = it.doctorEspecialidad,
                    pacienteId = it.pacienteId,
                    fechaHora = it.fechaHora,
                    motivo = it.motivo,
                    estado = it.estado,
                    codigoQr = it.codigoQr
                )
            }
            citaDao.clearCitas()
            citaDao.insertCitas(cacheList)
        }
        return response
    }

    override suspend fun crearCita(
        doctorId: String,
        fechaHora: String,
        motivo: String,
        notas: String?
    ): ApiResponse<CitaDto> {
        return safeApiCall { api.crearCita(CrearCitaRequest(doctorId, fechaHora, motivo, notas)) }
    }

    override suspend fun cancelarCita(citaId: String, motivo: String): ApiResponse<CitaDto> {
        return safeApiCall { api.cancelarCita(citaId, CancelarCitaRequest(motivo)) }
    }

    override suspend fun checkIn(citaId: String, code: String): ApiResponse<CitaDto> {
        return safeApiCall { api.checkIn(citaId, CheckInRequest(code)) }
    }

    // Prescriptions (Recetas)
    override suspend fun getRecetas(): ApiResponse<List<RecetaDto>> {
        return safeApiCall { api.getRecetas() }
    }

    override suspend fun crearReceta(
        citaId: String,
        diagnostico: String,
        observaciones: String?,
        detalles: List<DetalleRequest>
    ): ApiResponse<RecetaDto> {
        return safeApiCall { api.crearReceta(CrearRecetaRequest(citaId, diagnostico, observaciones, detalles)) }
    }

    // Medical History
    override suspend fun getHistorial(): ApiResponse<List<HistorialMedicoDto>> {
        return safeApiCall { api.getHistorial() }
    }

    override suspend fun crearHistorial(
        tipo: String,
        titulo: String,
        descripcion: String,
        fecha: String?,
        doctorNombre: String?,
        archivoUrl: String?
    ): ApiResponse<HistorialMedicoDto> {
        return safeApiCall {
            api.crearHistorial(
                CrearHistorialRequest(
                    tipo = tipo,
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = fecha,
                    doctorNombre = doctorNombre,
                    archivoUrl = archivoUrl
                )
            )
        }
    }

    // Notifications
    override suspend fun getNotificaciones(): ApiResponse<List<NotificacionDto>> {
        return safeApiCall { api.getNotificaciones() }
    }

    override suspend fun marcarNotificacionLeida(id: String): ApiResponse<Void> {
        return safeApiCall { api.marcarLeida(id) }
    }

    // Reviews
    override suspend fun crearValoracion(
        doctorId: String,
        calificacion: Int,
        comentario: String?
    ): ApiResponse<ValoracionDto> {
        return safeApiCall { api.crearValoracion(doctorId, CrearValoracionRequest(doctorId, calificacion, comentario)) }
    }

    override suspend fun getValoraciones(doctorId: String): ApiResponse<List<ValoracionDto>> {
        return safeApiCall { api.getValoraciones(doctorId) }
    }

    override suspend fun getAllUsers(): ApiResponse<List<UserDto>> {
        return safeApiCall { api.getAllUsers() }
    }

    override suspend fun toggleUserActive(userId: String): ApiResponse<UserDto> {
        return safeApiCall { api.toggleUserActive(userId) }
    }
}
