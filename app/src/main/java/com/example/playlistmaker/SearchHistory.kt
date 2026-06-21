package com.example.playlistmaker
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory (private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val historyKey = "search_history_key"
    private val maxHistorySize = 10

    private var historyList = mutableListOf<Track>()

    init {
        loadHistory()
    }
    fun addTrack(track:Track) {

        historyList.removeAll { it.trackId == track.trackId }

        historyList.add(0, track)


        if (historyList.size > maxHistorySize) {
            historyList.removeAt(historyList.lastIndex)
        }
        saveHistory()
    }
    fun getHistory(): List<Track> {
        return historyList.toList()
    }
    fun clearHistory() {
        historyList.clear()
        saveHistory()
    }
    private fun loadHistory() {
        val json = sharedPreferences.getString(historyKey, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = gson.fromJson(json, type) ?: mutableListOf()
        }
    }

    private fun saveHistory() {
        val json = gson.toJson(historyList)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}


