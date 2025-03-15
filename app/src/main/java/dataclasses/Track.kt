package dataclasses

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(@SerializedName("trackId") val trackId: Int,
                 @SerializedName("trackName") val trackName: String,
                 @SerializedName("artistName") val artistName: String,
                 @SerializedName("trackTimeMillis") val trackTime: Long,
                 @SerializedName("artworkUrl100") val artworkUrl100: String,
                 @SerializedName("collectionName") val collectionName: String?,
                 @SerializedName("country") val country: String?,
                 @SerializedName("primaryGenreName") val primaryGenreName: String?,
                 @SerializedName("releaseDate") val releaseDate: String?)
{

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast(COVER_DELIMITER, COVER_REPLACEMENT)

    fun getTrackTime(): String? = SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTime)

    fun getTrackYear(): String?{
        val parser = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
        val date = releaseDate?.let { parser.parse(it) } ?: return null
        val formatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }

    private companion object {
        const val TRACK_TIME_FORMAT = "mm:ss"
        const val INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val YEAR_FORMAT = "yyyy"
        const val COVER_DELIMITER = '/'
        const val COVER_REPLACEMENT = "512x512bb.jpg"
    }
}
