package com.a2b2.plog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String CHANNEL_ID = "fcm 테스트";
    private String CHANNEL_NAME = "커뮤니티 알림";


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //token을 서버로 전송
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }


        String title = remoteMessage.getNotification().getTitle(); //수신한 메시지 타이틀 얻어냄
        String body = remoteMessage.getNotification().getBody(); //수신한 메시지 바디(내용)를 얻어냄

        builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background);

        Notification notification = builder.build();
        try {
            notificationManager.notify(1, notification);
        } catch (SecurityException e) {
            // 권한이 없을 경우 예외 처리 (예: 사용자에게 안내 메시지를 표시)
            e.printStackTrace();
        }

    }
}
