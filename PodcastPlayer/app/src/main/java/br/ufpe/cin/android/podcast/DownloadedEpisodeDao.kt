package br.ufpe.cin.android.podcast

import androidx.room.*

@Dao
interface DownloadedEpisodeDao {
    @Query("SELECT * FROM downloadedEpisodes")
    fun getAll(): List<DownloadedEpisode>

    @Query(value = "SELECT * FROM downloadedEpisodes WHERE downloadLink = :downloadLink")
    fun getByDownloadLink(downloadLink: String): DownloadedEpisode?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg items: DownloadedEpisode)

    @Update
    fun updateAll(vararg items: DownloadedEpisode)

    @Delete
    fun deleteAll(vararg items: DownloadedEpisode)
}