package com.mediconnect.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mediconnect.app.data.local.entity.CitaCache
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {
    @Query("SELECT * FROM citas ORDER BY fecha DESC, hora DESC")
    fun getCitasFlow(): Flow<List<CitaCache>>

    @Query("SELECT * FROM citas ORDER BY fecha DESC, hora DESC")
    suspend fun getCitas(): List<CitaCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitas(citas: List<CitaCache>)

    @Query("DELETE FROM citas")
    suspend fun clearCitas()
}
