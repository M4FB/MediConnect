package com.mediconnect.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class DoctorCache(
    @PrimaryKey val id: String,
    val nombre: String,
    val apellido: String,
    val especialidad: String,
    val telefono: String?,
    val email: String,
    val promedioValoracion: Double?
)
