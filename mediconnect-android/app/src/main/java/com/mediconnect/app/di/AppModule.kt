package com.mediconnect.app.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.mediconnect.app.data.local.AppDatabase
import com.mediconnect.app.data.local.dao.CitaDao
import com.mediconnect.app.data.local.dao.DoctorDao
import com.mediconnect.app.data.remote.api.MediConnectApi
import com.mediconnect.app.data.remote.interceptor.AuthInterceptor
import com.mediconnect.app.data.repository.MediConnectRepository
import com.mediconnect.app.data.repository.MediConnectRepositoryImpl
import com.mediconnect.app.util.Constants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                Constants.PREFS_FILE,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            e.printStackTrace()
            context.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideMediConnectApi(okHttpClient: OkHttpClient): MediConnectApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MediConnectApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mediconnect_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDoctorDao(database: AppDatabase): DoctorDao = database.doctorDao()

    @Provides
    fun provideCitaDao(database: AppDatabase): CitaDao = database.citaDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMediConnectRepository(
        impl: MediConnectRepositoryImpl
    ): MediConnectRepository
}
