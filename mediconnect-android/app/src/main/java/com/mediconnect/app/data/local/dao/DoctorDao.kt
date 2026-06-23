package com.mediconnect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mediconnect.app.data.local.entity.DoctorCache
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
    @Query("SELECT * FROM doctors")
    fun getDoctorsFlow(): Flow<List<DoctorCache>>

    @Query("SELECT * FROM doctors")
    suspend fun getDoctors(): List<DoctorCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctors(doctors: List<DoctorCache>)

    @Query("DELETE FROM doctors")
    suspend fun clearDoctors()
}
