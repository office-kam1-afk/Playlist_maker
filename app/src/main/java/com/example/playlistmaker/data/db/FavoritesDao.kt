package com.example.playlistmaker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: TrackEntity)

    @Delete
    suspend fun delete(track: TrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY insertionOrder DESC")
    fun getAllFavorites(): Flow<List<TrackEntity>>

    // Метод должен называться getAllIds
    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getAllIds(): List<Long>
}