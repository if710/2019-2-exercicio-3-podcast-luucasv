package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tryDownloadDataAndDisplayList("https://feeds.soundcloud.com/users/soundcloud:users:39885437/sounds.rss")
    }

    // Sets recyclerView with old data, then tries to download the new one and update the recycler view
    fun tryDownloadDataAndDisplayList(url: String) {
        doAsync {
            // set recycler view with data already in database
            val database = ItemFeedDatabase.getDatabase(this@MainActivity)
            var itemList = database.itemFeedDao().getAll()

            uiThread {
                displayAdapterItems(itemList)
            }

            // download xml and update database
            try {
                downloadXmlAndUpdateDatabase(url)
            } catch (e: Exception) {
                // ignore connection errors
                Toast.makeText(this@MainActivity, "Unable to download new feed", Toast.LENGTH_SHORT).show()
            }

            // Update adapter list and update recycler view
            itemList = database.itemFeedDao().getAll()

            uiThread {
                displayAdapterItems(itemList)
            }
        }
    }

    fun downloadXmlAndUpdateDatabase(url: String) {
        val xml = URL(url).readText()
        val xmlItemList = Parser.parse(xml)
        val database = ItemFeedDatabase.getDatabase(this@MainActivity)
        database.itemFeedDao().insertAll(*xmlItemList.toTypedArray())
    }

    fun displayAdapterItems(itemFeedList: List<ItemFeed>) {
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.adapter = ItemFeedAdapter(itemFeedList)
    }
}
