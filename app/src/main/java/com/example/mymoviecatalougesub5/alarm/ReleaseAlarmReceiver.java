package com.example.mymoviecatalougesub5.alarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import com.example.mymoviecatalougesub5.R;
import com.example.mymoviecatalougesub5.activity.DetailMovieActivity;
import com.example.mymoviecatalougesub5.activity.MainActivity;
import com.example.mymoviecatalougesub5.model.Movie;
import java.util.Calendar;
import java.util.List;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class ReleaseAlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION = 102;
    public static final String NOTIFICATION_CHANNEL = "102";
    private static int getNotification = 2;
    private static final String INTENT_ID = "intent_id";
    private static final String INTENT_TITLE = "intent_title";

    public ReleaseAlarmReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(INTENT_ID, 0);
        Log.e("NotificationId", String.valueOf(notificationId));
        String title = intent.getStringExtra(INTENT_TITLE);
        Log.e("notificationId", title);
        showAlarmNotification(context, title, notificationId);
    }

    private void showAlarmNotification(Context context, String title, int notification){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notification, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle(title)
                .setContentText(String.valueOf(String.format(context.getString(R.string.notification_message), title)))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setSound(alarmRingtone);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL, "NOTOFICATION+CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});

            builder.setChannelId(NOTIFICATION_CHANNEL);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(notification, builder.build());
    }

    public void setRepeatingAlarm(Context context, List<Movie> movies){
        int delay = 0;
        for (Movie movie: movies){
            cancelAlarm(context);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ReleaseAlarmReceiver.class);
            intent.putExtra(INTENT_TITLE, movie.getTitle());
            intent.putExtra("id", "1000");
            Log.e("title", movie.getTitle());
            intent.putExtra(INTENT_ID, getNotification);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 2);
            calendar.set(Calendar.MINUTE,30);
            calendar.set(Calendar.SECOND, 0);

            int SDK_INT = Build.VERSION.SDK_INT;
            if (SDK_INT < Build.VERSION_CODES.KITKAT){
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis() + delay, pendingIntent);
            }
            else if (SDK_INT > Build.VERSION_CODES.KITKAT && SDK_INT < Build.VERSION_CODES.M){
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis()+delay,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );
            }
            else if (SDK_INT >= Build.VERSION_CODES.M){
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis() + delay, pendingIntent);
            }

            getNotification += 1;
            delay +=5000;
        }
    }

    public void cancelAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context));
    }

    private static PendingIntent getPendingIntent(Context context){
        Intent alarmIntent = new Intent(context, DailyAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, NOTIFICATION, alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
