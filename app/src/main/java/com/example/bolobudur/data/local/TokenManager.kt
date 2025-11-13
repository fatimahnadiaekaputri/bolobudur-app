package com.example.bolobudur.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_TOKEN = "jwt_token"
    }

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    // 🧠 Buat EncryptedSharedPreferences
    private val sharedPreferences = EncryptedSharedPreferences.create(
        PREFS_NAME,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Tambahkan Flow agar bisa dipantau di ViewModel
    private val _tokenFlow = MutableStateFlow(getToken())
    val tokenFlow: Flow<String?> = _tokenFlow.asStateFlow()

    // 🟢 Simpan token
    fun saveToken(token: String) {
        sharedPreferences.edit { putString(KEY_TOKEN, token) }
        _tokenFlow.value = token
    }

    // 🟢 Ambil token
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    // 🔴 Hapus token (saat logout)
    fun clearToken() {
        sharedPreferences.edit { remove(KEY_TOKEN) }
        _tokenFlow.value = null
    }
}
