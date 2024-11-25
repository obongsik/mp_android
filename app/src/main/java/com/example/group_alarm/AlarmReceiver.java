package com.example.group_alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String day = intent.getStringExtra("day"); // 요일 정보
        String type = intent.getStringExtra("type"); // 알림 타입 (daily or weekly)

        // 알림 생성
        String channelId = "alarm_channel";
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 알림 채널 생성 (Android 8.0 이상)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "알람 채널", NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        String contentText;
        if ("daily".equals(type)) {
            contentText = "오늘 설정된 알림이 울립니다!";
        } else {
            contentText = day + "에 설정된 알림이 울립니다!";
        }

        // 알림 빌더
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("알람")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // 알림 표시
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
