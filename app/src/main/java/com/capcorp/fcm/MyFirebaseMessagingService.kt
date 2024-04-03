package com.capcorp.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.detail_offer_accepeted.home_delivery.HomeDeliveryDetailDriverActivity
import com.capcorp.ui.driver.homescreen.detail_offer_completed.detail_delivery.HomeCompleteDriverActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.ui.user.homescreen.chat.chatmessage.ChatActivity
import com.capcorp.ui.user.homescreen.detail_offer_accepted.shop.ShopDetailsUserActivity
import com.capcorp.ui.user.homescreen.orders.request_detail.RequestDetailActivity
import com.capcorp.utils.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val requestID = Calendar.getInstance().timeInMillis.toInt()
    var intent: Intent? = null

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.e("TAG", "Refreshed token: " + refreshedToken!!)
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        SharedPrefs.with(applicationContext).save(DEVICE_TOKEN, refreshedToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("TAG", "From: " + remoteMessage.from!!)
        var message: String? = ""
        if (remoteMessage.data.size > 0) {
            Log.e("TAG", "Message data payload: " + remoteMessage.data)
            message = remoteMessage.data["status"]
        }
        if (remoteMessage.notification != null) {
            message = remoteMessage.notification!!.body
            Log.e("TAG", "Message Notification Body: " + remoteMessage.notification!!)
        }

        if (remoteMessage.data.containsKey("orderStatus")) {
            when (remoteMessage.data["orderStatus"]) {
                NotificationType.PICKUP -> {
                    if (remoteMessage.data.get("type").equals(OrderType.DELIVERY)) {
                        intent = Intent(this, ShopDetailsUserActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    } else if (remoteMessage.data.get("type").equals(OrderType.PICKUP)) {
                        intent = Intent(this, ShopDetailsUserActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    } else if (remoteMessage.data.get("type").equals(OrderType.SHOP)) {
                        intent = Intent(this, ShopDetailsUserActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    }
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.OFFER, NotificationType.CHANGE_OFFER, NotificationType.ACCEPT -> {
                    intent = Intent(this, RequestDetailActivity::class.java)
                        .putExtra(NOTIFICATION, true)
                        .putExtra("from_request", "false")
                        .putExtra(
                            ORDER_ID,
                            JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                        )
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.ACCEPTED -> {
                    if (remoteMessage.data.get("type").equals(OrderType.DELIVERY)) {
                        intent = Intent(this, HomeDeliveryDetailDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    } else if (remoteMessage.data.get("type").equals(OrderType.PICKUP)) {
                        intent = Intent(this, HomeDeliveryDetailDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    } else if (remoteMessage.data.get("type").equals(OrderType.SHOP)) {
                        intent = Intent(this, HomeDeliveryDetailDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )

                    }
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.COMPLETED -> {
                    if (remoteMessage.data.get("type").equals(OrderType.DELIVERY)) {
                        intent = Intent(this, HomeCompleteDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    } else if (remoteMessage.data.get("type").equals(OrderType.PICKUP)) {
                        intent = Intent(this, HomeCompleteDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    } else if (remoteMessage.data.get("type").equals(OrderType.SHOP)) {
                        intent = Intent(this, HomeCompleteDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    }
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.DELIVERED -> {
                    if (remoteMessage.data.get("type").equals(OrderType.DELIVERY)) {
                        intent = Intent(this, ShopDetailsUserActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    } else if (remoteMessage.data.get("type").equals(OrderType.PICKUP)) {
                        intent = Intent(this, ShopDetailsUserActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    } else if (remoteMessage.data.get("type").equals(OrderType.SHOP)) {
                        intent = Intent(this, ShopDetailsUserActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    }
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.PURCHASE_MADE -> {
                    if (remoteMessage.data.get("type").equals(OrderType.SHOP)) {
                        intent = Intent(this, ShopDetailsUserActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    }
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.ADDRESS_GIVEN -> {
                    if (remoteMessage.data.get("type").equals(OrderType.DELIVERY)) {
                        intent = Intent(this, HomeDeliveryDetailDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    }
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.COMMITED -> {
                    if (remoteMessage.data.get("type").equals("Pickup")) {
                        intent = Intent(this, HomeDeliveryDetailDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    }
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.ADD_RECEIPT -> {
                    if (remoteMessage.data.get("type").equals("Delivery")) {
                        intent = Intent(this, HomeDeliveryDetailDriverActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(
                                ORDER_ID,
                                JSONObject(remoteMessage.data as Map<*, *>).getString("orderId")
                            )
                    }
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.REJECTED -> {
                    intent = if (SharedPrefs.with(this).getString(USER_TYPE, "") == UserType.USER)
                        Intent(this, HomeActivity::class.java)
                    else
                        Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
                NotificationType.CANCEL -> {
                    intent = if (SharedPrefs.with(this).getString(USER_TYPE, "") == UserType.USER)
                        Intent(this, HomeActivity::class.java)
                    else
                        Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }

                else -> {
                    intent = if (SharedPrefs.with(this).getString(USER_TYPE, "") == UserType.USER)
                        Intent(this, HomeActivity::class.java)
                    else
                        Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                    intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                }
            }
        } else if (remoteMessage.data.containsKey("chatType")) {
            if (remoteMessage.data.get("chatType").equals("text")) {
                message =
                    JSONObject(remoteMessage.data as Map<*, *>).getString("senderFullName") + " " + "sent a new message"
                if (ChatActivity.onlineStatus == true && ChatActivity.receiverId.equals(
                        remoteMessage.data.get("senderId")
                    )
                ) {
                } else {
                    if (remoteMessage.data.containsKey("profilePicURL1")) {
                        intent = Intent(this, ChatActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(USER_ID, "")
                            .putExtra(
                                PROFILE_PIC_URL,
                                JSONObject(remoteMessage.data["profilePicURL1"]).getString("original")
                            )
                            .putExtra(
                                "fullname",
                                JSONObject(remoteMessage.data as Map<*, *>).getString("senderFullName")
                            )
                            .putExtra(
                                "receiverId",
                                JSONObject(remoteMessage.data as Map<*, *>).getString("senderId")
                            )
                        intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                    } else {
                        intent = Intent(this, ChatActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(USER_ID, "")
                            .putExtra(
                                "fullname",
                                JSONObject(remoteMessage.data as Map<*, *>).getString("senderFullName")
                            )
                            .putExtra(
                                "receiverId",
                                JSONObject(remoteMessage.data as Map<*, *>).getString("senderId")
                            )
                        intent?.let { sendPushNotification(remoteMessage, message ?: "", it) }
                    }
                }
            } else {
                message =
                    JSONObject(remoteMessage.data as Map<*, *>).getString("senderFullName") + " " + "sent an image"
                if (ChatActivity.onlineStatus == true && ChatActivity.receiverId.equals(
                        remoteMessage.data.get("senderId")
                    )
                ) {
                } else {
                    if (remoteMessage.data.containsKey("profilePicURL1")) {
                        intent = Intent(this, ChatActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(USER_ID, "")
                            .putExtra(
                                PROFILE_PIC_URL,
                                JSONObject(remoteMessage.data["profilePicURL1"]).getString("original")
                            )
                            .putExtra(
                                "fullname",
                                JSONObject(remoteMessage.data as Map<*, *>).getString("senderFullName")
                            )
                            .putExtra(
                                "receiverId",
                                JSONObject(remoteMessage.data as Map<*, *>).getString("senderId")
                            )
                        intent?.let { sendPushNotification(remoteMessage, message, it) }
                    } else {
                        intent = Intent(this, ChatActivity::class.java)
                            .putExtra(NOTIFICATION, true)
                            .putExtra(USER_ID, "")
                            .putExtra(
                                "fullname",
                                JSONObject(remoteMessage.data as Map<*, *>).getString("senderFullName")
                            )
                            .putExtra(
                                "receiverId",
                                JSONObject(remoteMessage.data as Map<*, *>).getString("senderId")
                            )
                        intent?.let { sendPushNotification(remoteMessage, message, it) }
                    }

                }
            }
        }
    }

    private fun sendPushNotification(
        remoteMessage: RemoteMessage,
        message: String,
        pendingIntent: Intent
    ) {
        if (intent != null) {
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            sendNotification(remoteMessage, message, pendingIntent, defaultSoundUri)
        }
    }

    private fun sendNotification(
        remoteMessage: RemoteMessage,
        message: String,
        pendingIntent: PendingIntent,
        defaultSoundUri: Uri
    ) {
        val channelID = getString(R.string.default_notification_channel_id)
        val icon1 = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(getNotificationIcon())
            .setLargeIcon(icon1)
            .setTicker("Qwerty")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.here2dare_green))
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelID, "here2dare_channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(requestID, notificationBuilder.build())
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.mipmap.ic_launcher_transparent else R.mipmap.ic_launcher
    }
}