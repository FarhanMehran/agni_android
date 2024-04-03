package com.agnidating.agni.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.agnidating.agni.R
import com.agnidating.agni.ui.activities.notifications.NotificationsActivity
import com.agnidating.agni.ui.fragment.chat.ChatActivity
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.logs
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Create by AJAY ASIJA on 04/28/2022
 */
@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val resultIntent = Intent(applicationContext, NotificationsActivity::class.java)

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
        remoteMessage.notification.toString().logs()
        if (sharedPrefs.getBoolean(CommonKeys.LOGGED_IN)) {
            if(remoteMessage.data.containsKey("sender_id")){
                sendChatNotification(remoteMessage.data)
                val intent=Intent(CommonKeys.CHAT_NOTIFICATION)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }else{
                sendNotification(remoteMessage.data, resultPendingIntent)
            }
        }
        super.onMessageReceived(remoteMessage)
    }

    private fun sendChatNotification(data: MutableMap<String, String>) {
        data.toString().logs()
        val resultIntent = Intent(applicationContext, ChatActivity::class.java)
        val userId= if (sharedPrefs.getString(CommonKeys.USER_ID)==data["receiver_id"]) data["sender_id"] else data["receiver_id"]
        sharedPrefs.save(CommonKeys.HAVE_NEW_MESSAGES,true)
        resultIntent.putExtra(CommonKeys.RECEIVER_ID,userId?.toInt())
        resultIntent.putExtra(CommonKeys.FROM_NOTIFICATION,true)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val notification =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setContentTitle(data["title"])
                .setContentText(data["message"])
                .setContentIntent(resultPendingIntent)
                .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
                .setSmallIcon(R.drawable.final_notification_icon)
                .setColor(ContextCompat.getColor(applicationContext,R.color.orange_pink))

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    "Agni Notification",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableLights(true)
                }
            )
        }
        manager.notify(0, notification.build())
    }


    private fun sendNotification(
        remoteMessage: Map<String, String>,
        resultPendingIntent: PendingIntent?
    ) {
        remoteMessage.toString().logs()
        val notification =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setContentTitle(remoteMessage["title"])
                .setContentText(remoteMessage["message"])
                .setContentIntent(resultPendingIntent)
                .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
                .setSmallIcon(R.drawable.final_notification_icon)
                .setColor(ContextCompat.getColor(applicationContext,R.color.orange_pink))

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    "Agni Notification",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableLights(true)
                }
            )
        }
        manager.notify(0, notification.build())
    }
}