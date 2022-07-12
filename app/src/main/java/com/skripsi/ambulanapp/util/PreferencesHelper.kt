package com.skripsi.ambulanapp.util

import android.content.Context
import android.content.SharedPreferences


class PreferencesHelper(context: Context) {
    companion object {

        const val PREF_USER_ID = "ID"
        const val PREF_USER_TYPE = "TYPE"
        const val PREF_IS_LOGIN = "LOGIN"

    }

    private val prefName = "PREFS_NAME"
    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    fun put(key: String, value: String) = editor.putString(key, value).apply()
    fun getString(key: String): String? = sharedPref.getString(key, null)
    fun put(key: String, value: Boolean) = editor.putBoolean(key, value).apply()
    fun getBoolean(key: String): Boolean = sharedPref.getBoolean(key, false)
    fun logout() = editor.clear().apply()

}