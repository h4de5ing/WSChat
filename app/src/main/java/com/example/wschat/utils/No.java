package com.example.wschat.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.example.wschat.MainActivity;
import com.example.wschat.R;


/**
 * https://github.com/android/user-interface-samples/blob/main/Notifications
 * * IMPORTANCE_NONE 关闭通知
 * * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
 * * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
 * * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
 * * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
 */
public class No {
    public static void no(Context context, String title, String text) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "10086");
        builder.setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_baseline_chat_24)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_baseline_chat_24))
                .setWhen(System.currentTimeMillis())//时间戳
                .setContentIntent(pi)//点击意图
                .setAutoCancel(true)//自动取消
                //.setSound(Uri.fromFile(new File("system/media/audio/notifications/Argon.ogg")))//自定义声音
                .setDefaults(NotificationCompat.DEFAULT_SOUND)//系统铃声
                .setVibrate(new long[]{0, 1000, 1000, 1000})//震动
                .setLights(Color.RED, 1000, 1000)//自定义LED灯
                //.setDefaults(NotificationCompat.DEFAULT_LIGHTS)//默认LED灯
                //.setDefaults(NotificationCompat.DEFAULT_ALL)//如果以上部分失效，全部采用默认设置
                .build();
        NotificationManager manger = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = null;
        //Android8.0要求设置通知渠道
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("foreground", "重要通知", NotificationManager.IMPORTANCE_HIGH);
            manger.createNotificationChannel(channel);
            builder.setChannelId("foreground");
        }
        manger.notify(0, builder.build());//id 用来取消通知
        //manger.cancel(0);//取消通知
    }
}
