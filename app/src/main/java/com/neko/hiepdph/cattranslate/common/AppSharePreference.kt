package com.neko.hiepdph.cattranslate.common

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor: SharedPreferences.Editor = edit()
    editor.func()
    editor.apply()
}


class AppSharePreference(private val context: Context?) {
    companion object {
        lateinit var INSTANCE: AppSharePreference

        @JvmStatic
        fun getInstance(context: Context?): AppSharePreference {
            if (!Companion::INSTANCE.isInitialized) {
                INSTANCE = AppSharePreference(context)
            }
            return INSTANCE
        }

    }

    fun saveLanguage(values: String) {
        saveString(Constant.KEY_LANGUAGE, values)
    }

    fun getSavedLanguage(defaultValues: String): String {
        return getString(Constant.KEY_LANGUAGE, defaultValues)
    }


    fun saveIsSetLangFirst(values: Boolean) {
        saveBoolean(Constant.KEY_SET_LANG, values)
    }

    fun getSetLangFirst(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_SET_LANG, defaultValues)
    }


    private fun saveLong(key: String, values: Long) = sharedPreferences().edit {
        putLong(key, values)
    }

    private fun getLong(key: String, defaultValues: Long): Long {
        return try {
            sharedPreferences().getLong(key, defaultValues)
        } catch (e: Exception) {
            sharedPreferences().edit { putLong(key, defaultValues) }
            defaultValues
        }
    }

    private fun saveInt(key: String, values: Int) = sharedPreferences().edit {
        putInt(key, values)
    }

    private fun getInt(key: String, defaultValues: Int): Int {
        return try {
            sharedPreferences().getInt(key, defaultValues)
        } catch (e: Exception) {
            sharedPreferences().edit { putInt(key, defaultValues) }
            defaultValues
        }
    }

    private fun saveString(key: String, values: String): Unit =
        sharedPreferences().edit { putString(key, values) }

    private fun getString(key: String, defaultValues: String): String {
        return try {
            sharedPreferences().getString(key, defaultValues)!!
        } catch (e: Exception) {
            sharedPreferences().edit { putString(key, defaultValues) }
            defaultValues
        }
    }


    private fun saveBoolean(key: String, values: Boolean) {
        sharedPreferences().edit { putBoolean(key, values) }
    }

    private fun getBoolean(key: String, defaultValues: Boolean): Boolean {
        return try {
            sharedPreferences().getBoolean(key, defaultValues)
        } catch (e: Exception) {
            sharedPreferences().edit { putBoolean(key, defaultValues) }
            defaultValues
        }
    }

    private fun saveStringSet(key: String, values: HashSet<String>) {
        sharedPreferences().edit { putStringSet(key, values) }
    }

    private fun getStringSet(key: String, defaultValues: HashSet<String>): HashSet<String> {
        return try {
            sharedPreferences().getStringSet(key, defaultValues)!! as HashSet
        } catch (e: Exception) {
            sharedPreferences().edit { putStringSet(key, defaultValues) }
            defaultValues
        }
    }

    fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener): Unit =
        sharedPreferences().registerOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )

    private fun defaultSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun sharedPreferences(): SharedPreferences = defaultSharedPreferences(context!!)


    fun saveTimeLogin(values: Int) {
        saveInt(Constant.KEY_RATE_SESSION, values)
    }

    fun getTimeLogin(defaultValues: Int): Int {
        return getInt(Constant.KEY_RATE_SESSION, defaultValues)
    }

    fun saveRateTimeLogin(values: Int) {
        saveInt(Constant.KEY_RATE_SESSION_D, values)
    }

    fun getRateTimeLogin(defaultValues: Int): Int {
        return getInt(Constant.KEY_RATE_SESSION_D, defaultValues)
    }

    fun saveUserRated(values: Boolean) {
        saveBoolean(Constant.KEY_RATE, values)
    }

    fun getUserRated(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_RATE, defaultValues)
    }


}

