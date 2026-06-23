package com.mediconnect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class CitaCache(
    @PrimaryKey val id: Long,
    val doctorId: Long,
    val doctorNombre: String,
    val doctorApellido: String,
    val doctorEspecialidad: String,
    val pacienteId: Long,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val estado: String,
    val codigoCheckIn: String?
)
