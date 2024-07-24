package com.example.guridownload.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.guridownload.downloadutils.Downloader
import java.util.concurrent.TimeUnit

class DownloadWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        if (Downloader(appContext).downloadFile(inputData.getString("url")!!) == -1L) {
            return Result.failure()
        }
        return Result.success()
    }

    companion object {

        fun scheduleWork(appContext: Context, url: String) {
            val constraint =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val workRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                .setConstraints(constraint)
                .setInitialDelay(30, TimeUnit.SECONDS)
                .setInputData(Data.Builder().putString("url", url).build())
                .build()

            WorkManager.getInstance(appContext).enqueue(workRequest)
        }
    }
}