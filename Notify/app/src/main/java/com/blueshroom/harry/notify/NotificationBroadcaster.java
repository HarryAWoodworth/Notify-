package com.blueshroom.harry.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/*
 * Created by Harry on 7/24/2017.
 */

public class NotificationBroadcaster extends BroadcastReceiver {

    public static String PROGRESS = "progress";
    public static String PROGRESS_END = "progressend";
    public String progress_end;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"GOT HERE!",Toast.LENGTH_LONG).show();

        // Create the Notification
        final NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_access_alarms)
                .setContentTitle(intent.getStringExtra("mTitleEditText"))
                .setContentText(intent.getStringExtra("mTextEditText"))
                .setColor(intent.getIntExtra("color", Color.LTGRAY));
        Notification notification = mBuilder.build();

        // Create a Notification Manager
        final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Get if the notification has a progress bar
        boolean hasProgress = intent.getBooleanExtra(PROGRESS, false);

        // Get the notification end message if the notification has a progress bar
        if(hasProgress) progress_end = intent.getStringExtra(PROGRESS_END);

        // Display the Notification
        notificationManager.notify(0,notification);

        // If it has a progress bar, update the progress bar over a period of time
        if(hasProgress) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            int incr;
                            for(incr = 0; incr <= 100; incr+=5) {
                                mBuilder.setProgress(100, incr, false);
                                notificationManager.notify(0,mBuilder.build());
                                try{
                                    Thread.sleep(2*1000);
                                } catch (InterruptedException e) {
                                    Log.d("SLEEP ERROR","Sleep Failure");
                                }
                            }
                            mBuilder.setContentText(progress_end)
                                    // Removes the progress bar
                                    .setProgress(0,0,false);
                            notificationManager.notify(0, mBuilder.build());
                        }
                    }
            ).start();
        }
    }
}
