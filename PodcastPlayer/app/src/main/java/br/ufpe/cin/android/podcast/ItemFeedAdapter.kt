package br.ufpe.cin.android.podcast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ItemFeedAdapter(private val itemFeedList: List<ItemFeed>) : RecyclerView.Adapter<ItemFeedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemlista, parent, false)
        return ItemFeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemFeedViewHolder, position: Int) {
        holder.bind(itemFeedList[position])
    }

    override fun getItemCount() = itemFeedList.size
}