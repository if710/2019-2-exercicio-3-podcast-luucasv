package br.ufpe.cin.android.podcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Binder
import android.os.Build
import androidx.core.app.NotificationCompat
import org.jetbrains.anko.doAsync

class PlayerService : Service() {
    private val mPlayer = MediaPlayer()
    private val mBinder = MusicBinder()
    private var playingEpisode: DownloadedEpisode? = null

    override fun onCreate() {
        super.onCreate()

        createChannel()

        mPlayer.setOnCompletionListener {
            doAsync {
                DownloadedEpisodeDatabase.getDatabase(applicationContext)
                    .downloadedEpisodeDao()
                    .deleteAll(playingEpisode!!)
            }
        }

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(
            applicationContext,"1")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true).setContentTitle("Podcast player running")
            .setContentText("Click here to open PodcastPlayer!")
            .setContentIntent(pendingIntent).build()

        startForeground(NOTIFICATION_ID, notification)
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        mPlayer.release()
        super.onDestroy()
    }

    fun isPlaying(): Boolean {
        return mPlayer.isPlaying
    }

    fun getPlayingEpisode(): DownloadedEpisode? {
        return playingEpisode
    }

    fun play(episode: DownloadedEpisode) {
        if (playingEpisode?.filePath != episode.filePath) {
            mPlayer.reset()
            mPlayer.setDataSource(episode.filePath)
            mPlayer.prepare()
            mPlayer.seekTo(episode.lastPause)
            playingEpisode = episode
        }

        mPlayer.start()
    }

    fun pause() {
        if (mPlayer.isPlaying) {
            mPlayer.pause()
            playingEpisode = DownloadedEpisode(playingEpisode!!.downloadLink, playingEpisode!!.filePath, mPlayer.currentPosition)
            doAsync {
                DownloadedEpisodeDatabase.getDatabase(applicationContext).downloadedEpisodeDao().updateAll(playingEpisode!!)
            }
        }
    }

    inner class MusicBinder : Binder() {
        internal val service: PlayerService
            get() = this@PlayerService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val mChannel = NotificationChannel("1", "PodcastPlayer notification channel", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "Notification for PodcastPlayer"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
    companion object {
        private val NOTIFICATION_ID = 2
    }
}