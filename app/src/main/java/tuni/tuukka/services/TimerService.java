package tuni.tuukka.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import tuni.tuukka.R;
import tuni.tuukka.activities.Timer;


public class TimerService extends Service {
    public final static String TIMER_EVENT_NAME = "time";
    public final static String TIMER_START_PARAM = "start";
    public final static String TIMER_SEND_PARAM = "seconds";
    public final static int SERVICE_ID = 14123;

    private boolean run;

    NotificationCompat.Builder builder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        run = true;

        new Thread(() -> {
            Intent broadcast = new Intent(TIMER_EVENT_NAME);
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
            int seconds = intent.getIntExtra(TIMER_START_PARAM, 0);

            while (run) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(run) {
                    seconds++;
                    broadcast.putExtra(TIMER_SEND_PARAM, seconds);
                    manager.sendBroadcast(broadcast);
                    builder.setContentText(DateUtils.formatElapsedTime(seconds));
                    NotificationManagerCompat.from(this).notify(SERVICE_ID, builder.build());
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        run = false;
        super.onDestroy();
    }
}
