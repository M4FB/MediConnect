package com.mediconnect.app.data.remote.interceptor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.mediconnect.app.MainActivity
import com.mediconnect.app.data.remote.dto.RefreshTokenRequest
import com.mediconnect.app.data.remote.dto.AuthResponse
import com.mediconnect.app.data.remote.dto.ApiResponse
import com.mediconnect.app.util.Constants
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip interception for open Auth endpoints to prevent loops
        val path = originalRequest.url.encodedPath
        if (path.endsWith("auth/login") || path.endsWith("auth/register") || path.endsWith("auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        val token = sharedPreferences.getString(Constants.KEY_ACCESS_TOKEN, null)
        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        var response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            val refreshToken = sharedPreferences.getString(Constants.KEY_REFRESH_TOKEN, null)
            if (refreshToken != null) {
                // Attempt to refresh the token synchronously
                val gson = com.google.gson.Gson()
                val refreshRequestBodyJson = gson.toJson(RefreshTokenRequest(refreshToken))
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody = RequestBody.create(mediaType, refreshRequestBodyJson)

                val refreshRequest = Request.Builder()
                    .url(Constants.BASE_URL + "auth/refresh")
                    .post(requestBody)
                    .build()

                try {
                    val refreshResponse = chain.proceed(refreshRequest)
                    if (refreshResponse.isSuccessful) {
                        val refreshResponseBodyString = refreshResponse.body?.string()
                        if (!refreshResponseBodyString.isNullOrEmpty()) {
                            val type = object : com.google.gson.reflect.TypeToken<ApiResponse<AuthResponse>>() {}.type
                            val apiResponse: ApiResponse<AuthResponse> = gson.fromJson(refreshResponseBodyString, type)
                            val newAuth = apiResponse.data

                            if (newAuth != null) {
                                // Save new session tokens
                                sharedPreferences.edit()
                                    .putString(Constants.KEY_ACCESS_TOKEN, newAuth.accessToken)
                                    .putString(Constants.KEY_REFRESH_TOKEN, newAuth.refreshToken)
                                    .apply()

                                refreshResponse.close()

                                // Retry the original request with new token
                                val newRequest = originalRequest.newBuilder()
                                    .header("Authorization", "Bearer ${newAuth.accessToken}")
                                    .build()

                                response.close()
                                return chain.proceed(newRequest)
                            }
                        }
                    }
                    refreshResponse.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Clear credentials if refresh fails or no refresh token is present
            sharedPreferences.edit()
                .remove(Constants.KEY_ACCESS_TOKEN)
                .remove(Constants.KEY_REFRESH_TOKEN)
                .apply()

            // Redirect to Login
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra("FORCE_LOGIN", true)
            }
            context.startActivity(intent)
        }

        return response
    }
}
