package br.ufpe.cin.android.podcast

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloadedEpisodes")
data class DownloadedEpisode(
    @PrimaryKey
    val downloadLink: String,
    val filePath: String
) {

    override fun toString(): String {
        return downloadLink
    }
}
