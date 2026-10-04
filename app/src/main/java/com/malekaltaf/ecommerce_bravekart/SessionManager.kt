package com.malekaltaf.ecommerce_bravekart

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

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
        fun hashPassword(password: String): String {
            val bytes = password.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}