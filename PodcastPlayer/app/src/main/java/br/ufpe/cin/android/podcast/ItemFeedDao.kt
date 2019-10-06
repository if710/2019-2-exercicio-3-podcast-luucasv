package br.ufpe.cin.android.podcast

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemFeedDao {
    @Query("SELECT * FROM itemsfeed")
    fun getAll(): List<ItemFeed>

    @Query(value = "SELECT * FROM itemsfeed WHERE downloadLink = :downloadLink")
    fun getByDownloadLink(downloadLink: String): ItemFeed

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: ItemFeed)
}