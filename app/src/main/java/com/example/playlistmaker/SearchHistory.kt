package com.example.playlistmaker
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory (private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val historyKey = "search_history_key"
    private val maxHistorySize = 10

    private val historySet = LinkedHashSet<Track>()
    private var historyList = ArrayList<Track>()
    init {
        loadHistory()
    }
    fun addTrack(track:Track) {

        historyList.removeAll { it.trackId == track.trackId }

        historyList.add(0, track)


        if (historyList.size > maxHistorySize) {
            historyList.removeAt(historyList.size - 1)
        }
        saveHistory()
    }
    fun getHistory(): List<Track> {
        return historyList
    }
    fun clearHistory() {
        historyList.clear()
        saveHistory()
    }
    private fun loadHistory() {
        val json = sharedPreferences.getString(historyKey, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = gson.fromJson(json, type)
        }
    }

    private fun saveHistory() {
        val json = gson.toJson(historyList)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}


