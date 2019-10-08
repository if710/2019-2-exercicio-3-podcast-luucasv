package br.ufpe.cin.android.podcast

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {
    internal var playerService: PlayerService? = null
    internal var isBound = false

    private val sConn = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            playerService = null
            isBound = false
        }

        override fun onServiceConnected(p0: ComponentName?, b: IBinder?) {
            val binder = b as PlayerService.MusicBinder
            playerService = binder.service
            isBound = true

        }
    }

    private val onDownloadCompleteEvent = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, i: Intent) {
            doAsync {
                // set recycler view with data already in database
                val database = ItemFeedDatabase.getDatabase(this@MainActivity)
                val itemList = database.itemFeedDao().getAll()

                uiThread {
                    displayAdapterItems(itemList)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val playerServiceIntent = Intent(this, PlayerService::class.java)
        startService(playerServiceIntent)

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
        if (playerService != null) {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = ItemFeedAdapter(itemFeedList, playerService!!)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isBound) {
            val bindIntent = Intent(this, PlayerService::class.java)
            isBound = bindService(bindIntent,sConn, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        unbindService(sConn)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        val f = IntentFilter(DownloadEpisodeService.DOWNLOAD_COMPLETE)
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(onDownloadCompleteEvent, f)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(onDownloadCompleteEvent)
    }
}
