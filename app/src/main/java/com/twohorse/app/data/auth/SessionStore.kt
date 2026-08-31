package com.twohorse.app.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val PREFS_NAME = "two_horse_session"
private const val KEY_TOKEN = "session_token"

/*
 * Session token storage, encrypted at rest. There is no revocation
 * list on the backend (the token is a stateless 30-day JWT), so the
 * only way to end a session locally is to delete the stored token.
 */
class SessionStore(
    context: Context
) {
    private val prefs: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(
                    MasterKey.KeyScheme.AES256_GCM
                )
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun getToken(): String? =
        prefs.getString(
            KEY_TOKEN,
            null
        )

    fun saveToken(
        token: String
    ) {
        prefs.edit()
            .putString(
                KEY_TOKEN,
                token
            )
            .apply()
    }

    fun clear() {
        prefs.edit()
            .remove(
                KEY_TOKEN
            )
            .apply()
    }
}
