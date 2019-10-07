package br.ufpe.cin.android.podcast

import android.app.IntentService
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class DownloadEpisodeService : IntentService("DownloadEpisodeService") {
    override fun onHandleIntent(intent: Intent?) {
        try {
            Log.d("Service", "start download")

            val root = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            root?.mkdirs()
            val output = File(root, intent!!.data!!.lastPathSegment)
            if (output.exists()) {
                output.delete()
            }
            val url = URL(intent.data!!.toString())
            val c = url.openConnection() as HttpURLConnection
            val fos = FileOutputStream(output.path)
            val out = BufferedOutputStream(fos)
            try {
                val `in` = c.inputStream
                val buffer = ByteArray(8192)
                var len = 0
                len = `in`.read(buffer)
                while (len >= 0) {
                    out.write(buffer, 0, len)
                    len = `in`.read(buffer)
                }
                out.flush()
            } finally {
                fos.fd.sync()
                out.close()
                c.disconnect()
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(DOWNLOAD_COMPLETE))

            val downloadedEpisode = DownloadedEpisode(url.toString(), output.path)

            DownloadedEpisodeDatabase.getDatabase(applicationContext)
                .downloadedEpisodeDao()
                .insertAll(downloadedEpisode)

            Log.d("Service", "done ${output.path}")
        } catch (e2: IOException) {
            Log.e(javaClass.name, "Exception durante download", e2)
        }
    }

    companion object {
        val DOWNLOAD_COMPLETE = "br.ufpe.cin.android.podcast.action.DOWNLOAD_COMPLETE"
    }
}