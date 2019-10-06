package br.ufpe.cin.android.podcast

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itemsfeed")
data class ItemFeed(
    val title: String,
    val link: String,
    val pubDate: String,
    val description: String,
    @PrimaryKey
    val downloadLink: String,
    val imageLink: String
) {

    override fun toString(): String {
        return title
    }
}
