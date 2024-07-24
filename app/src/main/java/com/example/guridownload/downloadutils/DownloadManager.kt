package com.example.guridownload.downloadutils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import java.util.Date

class Downloader(
    private val context: Context
) {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    fun downloadFile(url: String) : Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("application/pdf")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("PDF Downloaded")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${Date()}).pdf")

        return downloadManager.enqueue(request)
    }
}