package com.example.wschat.services

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.wschat.App
import com.example.wschat.R
import com.example.wschat.db.MessageItem
import com.example.wschat.utils.No
import com.example.wschat.ws.WSClient

class GuardService : Service() {
    override fun onCreate() {
        super.onCreate()
        println("启动 GuardService")
        //WSClient.getClient().retry(App.wsServer)
        WSClient.getClient().setWSMessageListener { message ->
            println("拿到进度，更新UI $message")
            receivedMessage(message)
            No.no(this, "收到来自服务器的消息", message)
        }
        createNotification()
    }

    private fun receivedMessage(message: String) {
        App.dao.insertMessage(
            MessageItem(
                0,
                "${System.currentTimeMillis()}",
                "server",
                message
            )
        )
    }

    private fun sendMessage(message: String) {
        App.dao.insertMessage(
            MessageItem(
                0,
                "${System.currentTimeMillis()}",
                "client",
                message
            )
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotification() {
        val pendingintent = PendingIntent.getActivity(
            this, 0,
            Intent(this, GuardService::class.java), 0
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            "Notification"
        )
            .setContentTitle("WebSocket后台服务")
            .setContentText("后台服务正在运行中")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingintent)
            .setSmallIcon(R.drawable.ic_baseline_chat_24)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.ic_baseline_chat_24
                )
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            val CHANNEL_ONE_ID = "CHANNEL_ONE_ID"
            val CHANNEL_ONE_NAME = "CHANNEL_ONE_ID"
            val notificationChannel = NotificationChannel(
                CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME,
                NotificationManager.IMPORTANCE_MIN
            )
            //如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.enableLights(false)
            //是否显示角标
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager?.createNotificationChannel(notificationChannel)
            builder.setChannelId(CHANNEL_ONE_ID)
        }
        startForeground(1, builder.build())
    }
}