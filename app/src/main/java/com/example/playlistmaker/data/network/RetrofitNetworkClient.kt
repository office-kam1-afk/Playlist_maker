package com.example.playlistmaker.data.network

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(
    private val iTunesApiService: ITunesApiService,
    private val context: Context
) {
        val api: ITunesApiService
        get() = iTunesApiService
}