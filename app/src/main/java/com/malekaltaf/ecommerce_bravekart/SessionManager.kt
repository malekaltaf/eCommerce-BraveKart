package com.malekaltaf.ecommerce_bravekart

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.security.SecureRandom

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("BraveKartSession", Context.MODE_PRIVATE)

    fun saveUserSession(userId: String, email: String, name: String) {
        prefs.edit().apply {
            putString("USER_ID", userId)
            putString("USER_EMAIL", email)
            putString("USER_NAME", name)
            putBoolean("IS_LOGGED_IN", true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("IS_LOGGED_IN", false)
    }

    fun getUserId(): String? {
        return prefs.getString("USER_ID", null)
    }

    fun getUserName(): String {
        return prefs.getString("USER_NAME", "User") ?: "User"
    }

    fun getUserEmail(): String {
        return prefs.getString("USER_EMAIL", "") ?: ""
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        /**
         * Generates a cryptographically random salt (hex string).
         * Call once at signup and store alongside the password hash in Firestore.
         */
        fun generateSalt(): String {
            val saltBytes = ByteArray(16)
            SecureRandom().nextBytes(saltBytes)
            return saltBytes.fold("") { str, it -> str + "%02x".format(it) }
        }

        /**
         * Hashes a password with a given salt using SHA-256.
         * At signup: generate a new salt via [generateSalt], store both salt and hash.
         * At login: retrieve the stored salt from Firestore, then call hashPassword(input, storedSalt).
         *
         * SHA-256(salt + password) prevents rainbow-table attacks on the stored hashes.
         */
        fun hashPassword(password: String, salt: String): String {
            val input = salt + password
            val bytes = input.toByteArray(Charsets.UTF_8)
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}
