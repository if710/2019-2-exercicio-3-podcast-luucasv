package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_episode_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.graphics.BitmapFactory
import java.net.URL


class EpisodeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        val downloadLink = intent.getStringExtra("item.downloadLink")
        itemTitle.text = downloadLink!!

        doAsync {
            val database = ItemFeedDatabase.getDatabase(this@EpisodeDetailActivity)
            val item = database.itemFeedDao().getByDownloadLink(downloadLink)
            val imageStream = URL(item.imageLink).openStream()
            val itemImageBitmap = BitmapFactory.decodeStream(imageStream)

            uiThread {
                itemImage.setImageBitmap(itemImageBitmap)
                itemTitle.text = item.title
                itemPubDate.text = item.pubDate
                itemDescription.text = item.description
                itemLink.text = item.link
            }
        }
    }
}
