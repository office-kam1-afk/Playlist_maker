package com.example.playlistmaker.di

import com.example.playlistmaker.domain.interactor.*
import org.koin.dsl.module

val interactorModule = module {
 single<SearchInteractor> { SearchInteractorImpl(get()) }
 single<HistoryInteractor> { HistoryInteractorImpl(get()) }
 single<SettingsInteractor> { SettingsInteractorImpl(get()) }

 single<FavoritesInteractor> { FavoritesInteractorImpl(get()) }
}