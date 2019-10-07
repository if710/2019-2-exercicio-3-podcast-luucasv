package br.ufpe.cin.android.podcast

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DownloadedEpisode::class), version = 1)
abstract class DownloadedEpisodeDatabase : RoomDatabase() {
    abstract fun downloadedEpisodeDao() : DownloadedEpisodeDao

    companion object {
        private var INSTANCE : DownloadedEpisodeDatabase? = null
        fun getDatabase(ctx : Context) : DownloadedEpisodeDatabase {
            if (INSTANCE == null) {
                synchronized(DownloadedEpisodeDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        DownloadedEpisodeDatabase::class.java,
                        "downloadedEpisodeDatabase.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}