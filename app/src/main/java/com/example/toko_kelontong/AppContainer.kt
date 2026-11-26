package com.example.toko_kelontong

import android.content.Context
import com.example.toko_kelontong.data.api.ApiService
import com.example.toko_kelontong.data.api.AuthInterceptor
import com.example.toko_kelontong.data.local.SessionManager
import com.example.toko_kelontong.data.repository.AuthRepository
import com.example.toko_kelontong.data.repository.CategoryRepository
import com.example.toko_kelontong.data.repository.ProductRepository
import com.example.toko_kelontong.data.repository.ReportRepository
import com.example.toko_kelontong.data.repository.TransactionRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Dependency injection manual (cukup untuk tugas ini; tanpa Hilt).
 * Dibuat sekali di [TokoApplication].
 */
class AppContainer(context: Context) {

    val sessionManager = SessionManager(context)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor(sessionManager))
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                )
            }
        }
        .build()

    private val api: ApiService = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    val authRepository = AuthRepository(api, sessionManager)
    val categoryRepository = CategoryRepository(api)
    val productRepository = ProductRepository(api)
    val transactionRepository = TransactionRepository(api)
    val reportRepository = ReportRepository(api)
}
