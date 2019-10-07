package br.ufpe.cin.android.podcast

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DownloadedEpisodeDao {
    @Query("SELECT * FROM downloadedEpisodes")
    fun getAll(): List<DownloadedEpisode>

    @Query(value = "SELECT * FROM downloadedEpisodes WHERE downloadLink = :downloadLink")
    fun getByDownloadLink(downloadLink: String): DownloadedEpisode

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: DownloadedEpisode)
}