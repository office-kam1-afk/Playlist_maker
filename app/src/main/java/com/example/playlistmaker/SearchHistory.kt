package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.domain.entity.Track
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val KEY_HISTORY = "search_history"
   private val MAX_HISTORY_SIZE = 10

    fun addTrack(track: Track) {

       val history = getHistory().toMutableList()

      history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }

    history.add(0, track)

   if (history.size > MAX_HISTORY_SIZE) {
     history.removeAt(MAX_HISTORY_SIZE)
}

 saveHistory(history)
 }

 fun getHistory(): List<Track> {
      return try {
       val json = sharedPreferences.getString(KEY_HISTORY, "[]")
     val type = object : TypeToken<List<Track>>() {}.type
   Gson().fromJson(json, type) ?: emptyList()
 } catch (e: JsonSyntaxException) {
    e.printStackTrace()
   clearHistory()
   emptyList()
 }
   }

  fun clearHistory() {
   saveHistory(emptyList())
 }

 private fun saveHistory(history: List<Track>) {
   val json = Gson().toJson(history)
 sharedPreferences.edit().putString(KEY_HISTORY, json).apply()
 }
}