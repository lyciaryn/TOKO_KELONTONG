package com.example.toko_kelontong.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.toko_kelontong.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

data class Session(
    val token: String? = null,
    val role: String? = null,
    val nama: String? = null,
    val email: String? = null,
) {
    val isLoggedIn: Boolean get() = !token.isNullOrBlank()
    val isOwner: Boolean get() = role.equals("pemilik", ignoreCase = true)
}

/**
 * Menyimpan token dan data user di DataStore.
 * Catatan: DataStore tidak terenkripsi; cukup untuk tugas, bukan untuk produksi.
 */
class SessionManager(context: Context) {

    private val store = context.applicationContext.sessionDataStore

    val session: Flow<Session> = store.data.map { prefs ->
        Session(
            token = prefs[KEY_TOKEN],
            role = prefs[KEY_ROLE],
            nama = prefs[KEY_NAMA],
            email = prefs[KEY_EMAIL],
        )
    }

    suspend fun save(token: String, user: User) {
        store.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_ROLE] = user.role
            prefs[KEY_NAMA] = user.nama
            prefs[KEY_EMAIL] = user.email
        }
    }

    suspend fun clear() {
        store.edit { it.clear() }
    }

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_ROLE = stringPreferencesKey("role")
        val KEY_NAMA = stringPreferencesKey("nama")
        val KEY_EMAIL = stringPreferencesKey("email")
    }
}
