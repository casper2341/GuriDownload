package com.example.guridownload.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import com.example.guridownload.NotificationActivity
import com.example.guridownload.R

object NotificationUtils {
    fun getDownloadCompleteNotification(context: Context, uri: Uri): Notification {
        val notificationIntent = Intent(context, NotificationActivity::class.java)
        notificationIntent.putExtra("uri", uri)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return getNotification(context, pendingIntent)
    }

    fun getNotification(context: Context, intent: PendingIntent): Notification {
        val builder = getNotificationBuilder(context, "guri")
            .setContentTitle("Sample PDF")
            .setContentIntent(intent)
            .setContentText("Download Complete")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_launcher_foreground))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setFlag(Notification.FLAG_AUTO_CANCEL, true)
        }

        return builder.build()
    }

    fun getNotificationBuilder(context: Context, channelId: String?) : Notification.Builder {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }

        if (channelId.equals("guri")) {
            val soundUri = getAlarmSoundUri()
            builder.setSound(soundUri)
        }
        return builder
    }

    private fun getAlarmSoundUri(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }
}