package com.example.playlistmaker.presentation.model

//import android.os.Parcelable
import com.example.playlistmaker.domain.entity.Track
//import kotlinx.parcelize.Parcelize

//@Parcelize
data class TrackUiModel(
    val trackName: String,
    val trackId: Long,
    val artistName: String,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) //: Parcelable

fun Track.toUiModel(): TrackUiModel {
    return TrackUiModel(
        trackName = this.trackName,
        trackId = this.trackId,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl
    )
}

//fun TrackUiModel.toDomain():  Track = Track(
  //  trackName = this.trackName,
   // trackId = this.trackId,
    //artistName = this.artistName,
   // trackTimeMillis = this.trackTimeMillis,
    ///artworkUrl100 = this.artworkUrl100,
    ///collectionName = this.collectionName,
   // releaseDate = this.releaseDate,
   // primaryGenreName = this.primaryGenreName,
   // country = this.country,
   // previewUrl = this.previewUrl
//)