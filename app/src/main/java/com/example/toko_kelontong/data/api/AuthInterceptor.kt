package com.example.toko_kelontong.data.api

import com.example.toko_kelontong.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Menempelkan "Authorization: Bearer <token>" ke setiap request.
 * Bila server membalas 401 pada request yang membawa token, sesi dihapus
 * sehingga UI otomatis kembali ke layar Login.
 *
 * Interceptor berjalan di thread jaringan OkHttp (bukan main thread),
 * jadi runBlocking di sini aman.
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionManager.session.first().token }

        val request = chain.request().newBuilder()
            .header("Accept", "application/json")
            .apply { if (!token.isNullOrBlank()) header("Authorization", "Bearer $token") }
            .build()

        val response = chain.proceed(request)

        if (response.code == 401 && !token.isNullOrBlank()) {
            runBlocking { sessionManager.clear() }
        }
        return response
    }
}
