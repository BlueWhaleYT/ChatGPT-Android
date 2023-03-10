package com.bluewhaleyt.chatgpt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // 設置默認通知頻道
            createNotificationChannel(context);

            // 啟用通知
            enableNotifications(context);
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("default", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Default Channel Description");
            manager.createNotificationChannel(channel);
        }
    }

    private void enableNotifications(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        boolean areNotificationsEnabled = manager.areNotificationsEnabled();
        if (!areNotificationsEnabled) {
            // 詢問用戶是否啟用通知
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("啟用通知");
            builder.setMessage("您是否希望啟用通知？");
            builder.setPositiveButton("是", (dialog, which) -> {
                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
                context.startActivity(intent);
            });
            builder.setNegativeButton("否", null);
            builder.show();
        }
    }
}