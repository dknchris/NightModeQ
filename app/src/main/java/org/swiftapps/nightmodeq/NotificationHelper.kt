package org.swiftapps.nightmodeq

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class NotificationHelper(private val context: Context) {

    private val channelId by lazy { "persistent_service" }
    val ntfId by lazy { 456 }
    private val ntfManager by lazy { context.getSystemService(NotificationManager::class.java)!! }

    fun createChannel() {
        val channelName = "Persistent service"
        val channelImportance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel(channelId, channelName, channelImportance)
        channel.setShowBadge(false)
        ntfManager.createNotificationChannel(channel)
    }

    fun createForegroundNotification(): Notification {
        val clickIntent = run {
            val intent = context.packageManager
                .getLaunchIntentForPackage(context.packageName)!!
                .setPackage(null)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        return Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_dark_theme)
            .setContentIntent(clickIntent)
            .setContentTitle("Quick tile for OnePlus Tones")
            .setContentText("Service running")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }
}