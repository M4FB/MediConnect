package com.mediconnect.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mediconnect.app.data.local.dao.CitaDao
import com.mediconnect.app.data.local.dao.DoctorDao
import com.mediconnect.app.data.local.entity.CitaCache
import com.mediconnect.app.data.local.entity.DoctorCache

@Database(entities = [DoctorCache::class, CitaCache::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doctorDao(): DoctorDao
    abstract fun citaDao(): CitaDao
}
