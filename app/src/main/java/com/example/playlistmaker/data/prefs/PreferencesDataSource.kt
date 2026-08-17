package com.example.playlistmaker.data.prefs

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesDataSource(private val sharedPreferences: SharedPreferences) {
  private val gson = Gson()
    private val historyKey = "search_history"
    private val themeKey = "dark_theme_enabled"

    fun getHistory(): List<TrackDto> {
        val json = sharedPreferences.getString(historyKey, "[]")
        val type = object : TypeToken<List<TrackDto>>() {}.type
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    fun saveHistory(history: List<TrackDto>){
        sharedPreferences.edit().putString(historyKey, gson.toJson(history)).apply()
    }
    fun getTheme(): Boolean = sharedPreferences.getBoolean(themeKey, false)
    fun setTheme(isDark: Boolean) = sharedPreferences.edit().putBoolean(themeKey, isDark).apply()
}

