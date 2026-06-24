package com.mediconnect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class CitaCache(
    @PrimaryKey val id: String,
    val doctorId: String,
    val doctorNombre: String,
    val doctorEspecialidad: String,
    val pacienteId: String,
    val fechaHora: String,
    val motivo: String,
    val estado: String,
    val codigoQr: String?
)
