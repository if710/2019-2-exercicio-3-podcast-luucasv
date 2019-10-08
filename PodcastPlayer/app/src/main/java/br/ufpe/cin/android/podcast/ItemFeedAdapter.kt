package br.ufpe.cin.android.podcast

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ItemFeedAdapter(private val itemFeedList: List<ItemFeed>, private val playerService: PlayerService) : RecyclerView.Adapter<ItemFeedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemlista, parent, false)
        return ItemFeedViewHolder(view, playerService)
    }

    override fun onBindViewHolder(holder: ItemFeedViewHolder, position: Int) {
        doAsync {
            val item = itemFeedList[position]
            val downloadedEpisode = DownloadedEpisodeDatabase.getDatabase(holder.itemView.context)
                .downloadedEpisodeDao()
                .getByDownloadLink(item.downloadLink)
            uiThread {
                holder.bind(item, downloadedEpisode)
            }
        }

    }

    override fun getItemCount() = itemFeedList.size
}