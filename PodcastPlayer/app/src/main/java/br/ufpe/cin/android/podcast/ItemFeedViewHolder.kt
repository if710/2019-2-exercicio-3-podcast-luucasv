package br.ufpe.cin.android.podcast

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.media.Image
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.image

class ItemFeedViewHolder(view: View, private val playerService: PlayerService) : RecyclerView.ViewHolder(view) {
    fun bind(item: ItemFeed, episode: DownloadedEpisode?) {
        val context = itemView.context
        val titleView = itemView.findViewById<TextView>(R.id.item_title)
        val dateView = itemView.findViewById<TextView>(R.id.item_date)
        val downloadButtonView = itemView.findViewById<Button>(R.id.item_download_action)
        val playButtonView = itemView.findViewById<ImageButton>(R.id.item_play_action)

        titleView.text = item.title
        titleView.setOnClickListener {
            val intent = Intent(context.applicationContext, EpisodeDetailActivity::class.java)
            intent.putExtra("item.downloadLink", item.downloadLink)
            context.startActivity(intent)
        }

        dateView.text = item.pubDate

        downloadButtonView.setOnClickListener {
            val intent = Intent(context.applicationContext, DownloadEpisodeService::class.java)
            intent.data = Uri.parse(item.downloadLink)

            context.startService(intent)
            Toast.makeText(context, "Downloading episode...", Toast.LENGTH_SHORT).show()
        }

        if (episode == null) {
            playButtonView.isEnabled = false
            playButtonView.isClickable = false
        } else {
            playButtonView.isEnabled = true
            playButtonView.isClickable = true
            playButtonView.setOnClickListener {
                if (playerService.isPlaying() && playerService.getPlayingEpisode()?.filePath == episode.filePath) {
                    playerService.pause()
                } else {
                    doAsync {
                        val curEpisode = DownloadedEpisodeDatabase.getDatabase(itemView.context)
                            .downloadedEpisodeDao()
                            .getByDownloadLink(episode.downloadLink)
                        playerService.play(curEpisode!!)
                    }
                }
            }
        }
    }
}