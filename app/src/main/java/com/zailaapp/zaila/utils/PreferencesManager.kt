package com.zailaapp.zaila.utils

import android.content.Context
import android.content.SharedPreferences
import com.zailaapp.zaila.ZailaApplication
import com.zailaapp.zaila.model.UserZaila

object PreferencesManager {

    private const val SHARED_PREFERENCES_NAME = "ZailaPreferences"

    private const val USER_TOKEN = "USER_TOKEN"
    private const val USER_PROFILE = "USER_PROFILE"

    private val preferences: SharedPreferences by lazy {
        ZailaApplication.instance.getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun saveUser(value: UserZaila?) {
        if (value != null) {
            preferences.putString(USER_PROFILE, JsonProvider.toJson(value))
        } else {
            preferences.putString(USER_PROFILE, null)
        }
    }

    fun getUser(): UserZaila {
        val userZaila: UserZaila = JsonProvider.fromJson(preferences.getString(USER_PROFILE))
            ?: return UserZaila()
        return userZaila
    }

    fun saveToken(value: String) {
        preferences.putString(USER_TOKEN, value)
    }

    fun getToken(): String {
        return preferences.getString(USER_TOKEN)
    }

    fun isLogged(): Boolean {
        val token = getToken()
        return token != null && !token.isEmpty()
    }

    fun clearLocalStorage() {
        preferences.edit()
            .clear()
            .apply()
    }

    fun logout() {

        //todo - clear anything else related to login
        saveUser(null)
        saveToken("")

    }
}

private fun SharedPreferences.putString(key: String?, value: String?) {
    edit().putString(key, value).apply()
}

private fun SharedPreferences.getString(key: String?): String {
    return if (key.isNullOrEmpty() || contains(key).not()) ""
    else getString(key, "") ?: ""
}

private fun SharedPreferences.putBoolean(key: String?, value: Boolean) {
    edit().putBoolean(key, value).apply()
}

private fun SharedPreferences.getBoolean(key: String?): Boolean {
    return getBoolean(key, false)
}

private fun SharedPreferences.putLong(key: String?, value: Long) {
    edit().putLong(key, value).apply()
}

private fun SharedPreferences.putInt(key: String?, value: Int) {
    edit().putInt(key, value).apply()
}

private fun SharedPreferences.getLong(key: String?): Long {
    return getLong(key, 0)
}
