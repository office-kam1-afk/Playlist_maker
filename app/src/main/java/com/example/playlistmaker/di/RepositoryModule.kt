package com.example.playlistmaker.di

import com.example.playlistmaker.data.repository_impl.FavoritesRepositoryImpl
import com.example.playlistmaker.domain.repository.FavoritesRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
}