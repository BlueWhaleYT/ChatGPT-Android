package com.bluewhaleyt.chatgpt.utils;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bluewhaleyt.chatgpt.R;
import com.bluewhaleyt.common.SDKUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NotificationUtil {
    private static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "ChatGPT Notification";
    private static final String CHANNEL_DESCRIPTION = "ChatGPT Notification";

    private Context mContext;
    private NotificationManagerCompat mNotificationManager;

    public NotificationUtil(Context context) {
        mContext = context;
        mNotificationManager = NotificationManagerCompat.from(context);

        // 創建 notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(String title, String content, Intent intent) {
        // 設置 PendingIntent.FLAG_IMMUTABLE 標誌
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        var priority = 0;
        if (SDKUtil.isAtLeastSDK24()) priority = NotificationManager.IMPORTANCE_HIGH;
        else priority = NotificationCompat.PRIORITY_HIGH;
        // 創建 NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(priority);

        // 顯示通知
        mNotificationManager.notify(0, builder.build());
    }

    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}