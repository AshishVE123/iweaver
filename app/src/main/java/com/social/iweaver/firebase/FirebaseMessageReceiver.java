package com.social.iweaver.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.social.iweaver.R;
import com.social.iweaver.activity.MainActivity;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.CustomLoader;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    private UserPreference userPreference;

    @Override
    public void onNewToken(@NonNull String token) {
        userPreference = UserPreference.getInstance(this);
        userPreference.setFCMToken(token);
        Log.d("TAG", "Refreshed token: " + token);
    }

    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
    }


    private RemoteViews getCustomDesign(String title,
                                        String message) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.text, message);
        remoteViews.setImageViewResource(R.id.appBarParam,
                R.drawable.app_icon);
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title,
                                 String message) {
        Intent intent = null;
        if (CustomLoader.isAppForground(this)) {
            intent = new Intent();
        } else {
            intent = new Intent(this, MainActivity.class);
        }
       /* Intent intent
                = new Intent(this, MainActivity.class);*/
        String channel_id = "notification_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.ic_app_logo)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.ic_app_logo))
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    /*.setSmallIcon(R.drawable.app_icon)*/.
                    setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.ic_app_logo));
            builder = builder.setContent(
                    getCustomDesign(title, message));
        } else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    /*.setSmallIcon(R.drawable.app_icon)*/.
                    setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.ic_app_logo));
            ;
        }
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }
}