package com.example.toko_kelontong.ui.common

import android.util.Patterns

/** Validasi input di sisi aplikasi (FR-10). Mengembalikan pesan error, atau null bila valid. */
object Validators {

    fun required(value: String, label: String): String? =
        if (value.isBlank()) "$label wajib diisi" else null

    fun email(value: String): String? = when {
        value.isBlank() -> "Email wajib diisi"
        !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() -> "Format email tidak valid"
        else -> null
    }

    fun password(value: String): String? = when {
        value.isBlank() -> "Password wajib diisi"
        value.length < MIN_PASSWORD -> "Password minimal $MIN_PASSWORD karakter"
        else -> null
    }

    private const val MIN_PASSWORD = 6
}
