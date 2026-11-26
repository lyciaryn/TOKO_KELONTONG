package com.example.toko_kelontong.data.repository

import com.example.toko_kelontong.data.model.ApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException

/** Hasil pemanggilan API yang sudah dibungkus agar ViewModel tidak perlu mengurus exception. */
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T?, val message: String?) : ApiResult<T>
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>
}

private val gson = Gson()

/**
 * Menjalankan panggilan Retrofit dan menerjemahkan semua kegagalan
 * (jaringan, HTTP 4xx/5xx, JSON rusak) menjadi pesan yang ramah untuk pengguna.
 * Menjaga aplikasi tidak crash saat jaringan gagal (PRD C6).
 */
suspend fun <T> safeApiCall(block: suspend () -> Response<ApiResponse<T>>): ApiResult<T> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            val body = response.body()
            ApiResult.Success(body?.data, body?.message)
        } else {
            ApiResult.Error(parseErrorMessage(response), response.code())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        ApiResult.Error("Tidak dapat terhubung ke server. Periksa koneksi dan alamat server.")
    } catch (e: Exception) {
        ApiResult.Error("Terjadi kesalahan: ${e.message ?: "tidak diketahui"}")
    }
}

private fun parseErrorMessage(response: Response<*>): String {
    val fromServer = try {
        response.errorBody()?.string()?.let { gson.fromJson(it, ApiResponse::class.java)?.message }
    } catch (_: Exception) {
        null
    }
    if (!fromServer.isNullOrBlank()) return fromServer

    return when (response.code()) {
        400 -> "Permintaan tidak valid."
        401 -> "Sesi berakhir atau email/password salah."
        403 -> "Anda tidak memiliki akses untuk aksi ini."
        404 -> "Data tidak ditemukan."
        422 -> "Data yang dikirim tidak valid."
        in 500..599 -> "Server sedang bermasalah. Coba lagi nanti."
        else -> "Gagal (kode ${response.code()})."
    }
}
