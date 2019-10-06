package br.ufpe.cin.android.podcast

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ItemFeed::class), version = 1)
abstract class ItemFeedDatabase : RoomDatabase() {
    abstract fun itemFeedDao() : ItemFeedDao

    companion object {
        private var INSTANCE : ItemFeedDatabase? = null
        fun getDatabase(ctx : Context) : ItemFeedDatabase {
            if (INSTANCE == null) {
                synchronized(ItemFeedDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemFeedDatabase::class.java,
                        "itemsFeed.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}