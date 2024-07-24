package com.example.guridownload.downloadutils

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.URL

class DownloadFile {
    companion object {
        suspend fun saveFile(context: Context, url: URL, uri: Uri) {
            withContext(Dispatchers.IO) {
                val connection = url.openConnection()
                connection.connect()

                val inputStream = BufferedInputStream(connection.getInputStream())
                val outputStream = context.contentResolver.openOutputStream(uri)

                outputStream?.use { output ->
                    inputStream.use { inputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                        }
                    }
                }
            }
        }
    }
}