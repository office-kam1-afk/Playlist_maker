package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.FavoritesDao
import com.example.playlistmaker.data.network.ITunesApiService
import com.example.playlistmaker.data.prefs.PreferencesDataSource
import com.example.playlistmaker.data.repository_impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SearchRepositoryImpl
import com.example.playlistmaker.data.repository_impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.google.gson.Gson
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    factory { Gson() }
    single {
        val context = get<Context>()
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }
    single { PreferencesDataSource(get()) }


    single {
        Room.databaseBuilder(
            get<Context>(),
            AppDatabase::class.java,
            "playlist_maker.db"
        ).fallbackToDestructiveMigration().build()
    }

    single<FavoritesDao> {
        val db = get<com.example.playlistmaker.data.db.AppDatabase>()
        db.favoritesDao()
    }

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
   }
    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }
    single<HistoryRepository> {
        HistoryRepositoryImpl(get(), get())
    }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}